package com.logos.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.logos.domain.dto.RuleGenerateRequest;
import com.logos.domain.dto.RuleGenerateResponse;
import com.logos.domain.dto.RuleValidateRequest;
import com.logos.domain.dto.RuleValidateResponse;
import com.logos.domain.dto.RuleUpdateRequest;
import com.logos.domain.dto.RuleUpdateResponse;
import com.logos.domain.dto.RuleExecuteRequest;
import com.logos.domain.dto.RuleExecuteResponse;
import com.logos.domain.entity.BusinessConstraintNode;
import com.logos.domain.entity.MetadataNode;
import com.logos.domain.entity.RuleInstanceNode;
import com.logos.engine.spel.SpelExecutionEngine;
import com.logos.engine.spel.SpelExecutionResult;
import com.logos.llm.LlmClient;
import com.logos.llm.PromptTemplates;
import com.logos.repository.BusinessConstraintRepository;
import com.logos.repository.MetadataRepository;
import com.logos.repository.RuleInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 规则服务实现
 * 对齐规范 4.4 章节 - 高性能缓存及标准 OpenAPI 接口
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final LlmClient llmClient;
    private final SpelExecutionEngine spelEngine;
    private final MetadataRepository metadataRepository;
    private final BusinessConstraintRepository constraintRepository;
    private final RuleInstanceRepository ruleInstanceRepository;

    // 预编译 SpEL 缓存，满足高峰期响应 <= 3s 要求
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
    private final SpelExpressionParser spelParser = new SpelExpressionParser();

    @Override
    public RuleGenerateResponse generate(RuleGenerateRequest request) {
        log.info("开始生成规则，产品ID: {}, 自然语言: {}", request.getProductId(), request.getNaturalLanguage());
        List<String> traceSteps = new ArrayList<>();
        traceSteps.add("业务需求(" + request.getNaturalLanguage() + ")");

        try {
            // 1. 从 Neo4j 获取本体上下文
            OntologyContextResult ontologyResult = buildOntologyContextWithTrace(request.getProductId());
            String ontologyContext = ontologyResult.context;
            traceSteps.addAll(ontologyResult.traceSteps);
            log.debug("本体上下文: {}", ontologyContext);

            // 2. 构建 LLM 请求
            String userMessage = PromptTemplates.buildNl2SpelUserMessage(
                    request.getNaturalLanguage(), ontologyContext);

            // 3. 调用 LLM 生成 SpEL
            String llmResponse = llmClient.chat(PromptTemplates.NL2SPEL_SYSTEM, userMessage);
            log.debug("LLM 响应: {}", llmResponse);

            // 4. 解析 LLM 响应并添加 trace
            RuleGenerateResponse response = parseLlmResponse(llmResponse);
            response.setTrace(String.join(" -> ", traceSteps));
            return response;
        } catch (Exception e) {
            log.error("规则生成失败", e);
            return RuleGenerateResponse.builder()
                    .spel("")
                    .explanation("生成失败: " + e.getMessage())
                    .confidence(0.0)
                    .evidenceNodes(List.of())
                    .trace(String.join(" -> ", traceSteps) + " -> 生成失败")
                    .build();
        }
    }

    @Override
    public RuleValidateResponse validate(RuleValidateRequest request) {
        log.info("开始验证规则，SpEL: {}", request.getSpel());
        List<String> traceSteps = new ArrayList<>();
        traceSteps.add("执行上下文(RuleContext)");

        // 记录变量绑定
        if (request.getMockData() != null) {
            String varBindings = request.getMockData().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(", "));
            traceSteps.add("变量绑定(" + varBindings + ")");
        }

        // 执行 SpEL
        SpelExecutionResult result = spelEngine.execute(request.getSpel(), request.getMockData());
        traceSteps.add("条件匹配(" + result.getResult() + ")");

        return RuleValidateResponse.builder()
                .success(result.isSuccess())
                .actualValue(result.getResult())
                .logs(result.getLogs())
                .trace(String.join(" -> ", traceSteps))
                .build();
    }

    @Override
    public RuleUpdateResponse updateRule(RuleUpdateRequest request) {
        log.info("开始更新规则，规则ID: {}", request.getRuleId());
        List<String> traceSteps = new ArrayList<>();
        traceSteps.add("规则更新(" + request.getRuleId() + ")");

        try {
            // 1. 查找现有规则
            Optional<RuleInstanceNode> existingRule = ruleInstanceRepository.findById(request.getRuleId());
            String newVersion;
            RuleInstanceNode rule;

            if (existingRule.isPresent()) {
                rule = existingRule.get();
                // 版本号递增
                String currentVersion = rule.getVersion() != null ? rule.getVersion() : "v1.0.0";
                newVersion = incrementVersion(currentVersion);
                traceSteps.add("版本升级(" + currentVersion + " -> " + newVersion + ")");
            } else {
                // 创建新规则
                rule = new RuleInstanceNode();
                rule.setId(request.getRuleId());
                rule.setCreatedAt(Instant.now().toString());
                newVersion = "v1.0.0";
                traceSteps.add("新建规则(版本 " + newVersion + ")");
            }

            // 2. 更新规则属性
            if (request.getName() != null) rule.setName(request.getName());
            rule.setSpel(request.getSpel());
            if (request.getPriority() != null) rule.setPriority(request.getPriority());
            if (request.getCategory() != null) rule.setCategory(request.getCategory());
            if (request.getErrorMessage() != null) rule.setErrorMessage(request.getErrorMessage());
            if (request.getTargetProductId() != null) rule.setTargetProductId(request.getTargetProductId());
            rule.setVersion(newVersion);
            rule.setUpdatedAt(Instant.now().toString());

            // 3. 决定发布状态
            String status = Boolean.TRUE.equals(request.getPublish()) ? "PUBLISHED" : "DRAFT";
            rule.setStatus(status);
            traceSteps.add("状态设置(" + status + ")");

            // 4. 保存到 Neo4j
            ruleInstanceRepository.save(rule);

            // 5. 如果发布，更新缓存
            if ("PUBLISHED".equals(status)) {
                Expression exp = spelParser.parseExpression(request.getSpel());
                expressionCache.put(request.getRuleId(), exp);
                traceSteps.add("缓存更新(热发布完成)");
            }

            return RuleUpdateResponse.builder()
                    .ruleId(request.getRuleId())
                    .version(newVersion)
                    .status(status)
                    .updatedAt(rule.getUpdatedAt())
                    .message("规则已成功" + ("PUBLISHED".equals(status) ? "热发布到缓存" : "保存为草稿"))
                    .trace(String.join(" -> ", traceSteps))
                    .build();
        } catch (Exception e) {
            log.error("规则更新失败", e);
            return RuleUpdateResponse.builder()
                    .ruleId(request.getRuleId())
                    .status("FAILED")
                    .message("更新失败: " + e.getMessage())
                    .trace(String.join(" -> ", traceSteps) + " -> 更新失败")
                    .build();
        }
    }

    @Override
    public RuleExecuteResponse executeRule(RuleExecuteRequest request) {
        log.info("开始执行规则，规则ID: {}", request.getRuleId());
        long startTime = System.currentTimeMillis();
        List<String> logs = new ArrayList<>();
        List<String> traceSteps = new ArrayList<>();
        traceSteps.add("规则执行(" + request.getRuleId() + ")");

        try {
            // 1. 获取规则
            Optional<RuleInstanceNode> ruleOpt = ruleInstanceRepository.findById(request.getRuleId());
            if (ruleOpt.isEmpty()) {
                return RuleExecuteResponse.block("规则不存在: " + request.getRuleId(),
                        System.currentTimeMillis() - startTime, logs,
                        String.join(" -> ", traceSteps) + " -> 规则不存在");
            }

            RuleInstanceNode rule = ruleOpt.get();
            logs.add("找到规则: " + rule.getName());
            traceSteps.add("命中规则(" + rule.getName() + ")");

            // 2. 从缓存获取或编译表达式
            Expression exp = expressionCache.computeIfAbsent(request.getRuleId(),
                    k -> spelParser.parseExpression(rule.getSpel()));
            logs.add("SpEL 表达式: " + rule.getSpel());

            // 3. 执行规则
            SpelExecutionResult result = spelEngine.execute(rule.getSpel(), request.getPayload());
            logs.addAll(result.getLogs());
            traceSteps.add("执行结果(" + result.getResult() + ")");

            long executionTime = System.currentTimeMillis() - startTime;

            // 4. 判断结果
            if (result.isSuccess() && Boolean.TRUE.equals(result.getResult())) {
                return RuleExecuteResponse.pass(result.getResult(), executionTime, logs,
                        String.join(" -> ", traceSteps));
            } else {
                return RuleExecuteResponse.block(rule.getErrorMessage(), executionTime, logs,
                        String.join(" -> ", traceSteps));
            }
        } catch (Exception e) {
            log.error("规则执行失败", e);
            return RuleExecuteResponse.block("执行异常: " + e.getMessage(),
                    System.currentTimeMillis() - startTime, logs,
                    String.join(" -> ", traceSteps) + " -> 执行异常");
        }
    }

    /**
     * 构建本体上下文（带溯源）
     */
    private OntologyContextResult buildOntologyContextWithTrace(String productId) {
        Map<String, Object> context = new HashMap<>();
        List<String> traceSteps = new ArrayList<>();

        // 获取元数据
        List<MetadataNode> metadataList = new ArrayList<>();
        metadataList.addAll(metadataRepository.findByEntityCode("RuleContext"));
        metadataList.addAll(metadataRepository.findByEntityCode("ProdInst"));

        Map<String, String> metadataMap = new HashMap<>();
        for (MetadataNode m : metadataList) {
            metadataMap.put(m.getId(), m.getName() + " (路径: " + m.getPath() + ")");
        }
        context.put("metadata", metadataMap);

        if (!metadataList.isEmpty()) {
            String paths = metadataList.stream()
                    .map(m -> m.getId() + ":" + m.getPath())
                    .collect(Collectors.joining(", "));
            traceSteps.add("匹配属性(" + paths + ")");
        }

        // 获取业务约束
        List<BusinessConstraintNode> constraints = constraintRepository.findByProductId(productId);
        context.put("constraints", constraints);

        if (!constraints.isEmpty()) {
            traceSteps.add("命中实体(ProdInst:" + productId + ")");
            for (BusinessConstraintNode bc : constraints) {
                traceSteps.add("约束条件(" + bc.getName() + ")");
            }
        }

        return new OntologyContextResult(JSON.toJSONString(context), traceSteps);
    }

    /**
     * 构建本体上下文
     */
    private String buildOntologyContext(String productId) {
        return buildOntologyContextWithTrace(productId).context;
    }

    /**
     * 版本号递增
     */
    private String incrementVersion(String version) {
        try {
            String[] parts = version.replace("v", "").split("\\.");
            int patch = Integer.parseInt(parts[2]) + 1;
            return "v" + parts[0] + "." + parts[1] + "." + patch;
        } catch (Exception e) {
            return "v1.0.1";
        }
    }

    /**
     * 本体上下文结果（包含 trace）
     */
    private static class OntologyContextResult {
        final String context;
        final List<String> traceSteps;

        OntologyContextResult(String context, List<String> traceSteps) {
            this.context = context;
            this.traceSteps = traceSteps;
        }
    }

    /**
     * 解析 LLM 响应
     */
    private RuleGenerateResponse parseLlmResponse(String llmResponse) {
        try {
            // 尝试从响应中提取 JSON
            String jsonStr = extractJson(llmResponse);
            JSONObject json = JSON.parseObject(jsonStr);

            return RuleGenerateResponse.builder()
                    .spel(json.getString("spel"))
                    .explanation(json.getString("explanation"))
                    .confidence(json.getDouble("confidence"))
                    .evidenceNodes(json.getList("evidenceNodes", String.class))
                    .build();
        } catch (Exception e) {
            log.warn("解析 LLM 响应失败，使用原始响应", e);
            return RuleGenerateResponse.builder()
                    .spel(llmResponse)
                    .explanation("自动生成")
                    .confidence(0.5)
                    .evidenceNodes(List.of())
                    .build();
        }
    }

    /**
     * 从文本中提取 JSON
     */
    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
