package com.logos.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.logos.domain.dto.*;
import com.logos.domain.entity.*;
import com.logos.engine.spel.SpelValidationService;
import com.logos.llm.LlmClient;
import com.logos.llm.PromptTemplates;
import com.logos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则服务
 * 处理NL2SpEL生成、规则验证、规则管理等核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService {
    
    private final LlmClient llmClient;
    private final SpelValidationService spelValidationService;
    private final MetadataRepository metadataRepository;
    private final ProductRepository productRepository;
    private final EnumValueRepository enumValueRepository;
    private final RuleRepository ruleRepository;
    private final BusinessConstraintRepository constraintRepository;
    
    /**
     * 根据自然语言生成SpEL表达式
     */
    @Transactional(readOnly = true)
    public RuleGenerateResponse generateRule(RuleGenerateRequest request) {
        log.info("开始生成规则, productId={}, nl={}", request.getProductId(), request.getNaturalLanguage());
        
        // 1. 查询产品信息
        ProductNode product = productRepository.findByProdId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("产品不存在: " + request.getProductId()));
        
        // 2. 查询本体知识（RAG检索）
        List<MetadataNode> metadataList = metadataRepository.findByProductId(request.getProductId());
        List<EnumValueNode> enumValues = new ArrayList<>();
        for (MetadataNode meta : metadataList) {
            enumValues.addAll(enumValueRepository.findByAttributeId(meta.getMetaId()));
        }
        
        // 3. 构建提示词
        String ontologyContext = buildOntologyContext(metadataList, enumValues);
        String userPrompt = String.format(
                PromptTemplates.NL2SPEL_USER_TEMPLATE,
                ontologyContext,
                product.getProdId(),
                product.getName(),
                request.getNaturalLanguage()
        );
        
        // 4. 调用LLM生成
        String llmResponse = llmClient.chat(PromptTemplates.NL2SPEL_SYSTEM, userPrompt);
        
        // 5. 解析响应
        RuleGenerateResponse response = parseLlmResponse(llmResponse, metadataList);
        
        // 6. 验证SpEL语法
        if (!spelValidationService.validateSyntax(response.getSpel())) {
            String error = spelValidationService.getSyntaxError(response.getSpel());
            log.warn("生成的SpEL语法错误: {}", error);
            response.setConfidence(response.getConfidence() * 0.5);
        }
        
        log.info("规则生成完成: {}", response.getSpel());
        return response;
    }
    
    /**
     * 验证SpEL表达式
     */
    public RuleValidateResponse validateRule(RuleValidateRequest request) {
        log.info("开始验证规则: {}", request.getSpel());
        
        // 1. 语法检查
        if (!spelValidationService.validateSyntax(request.getSpel())) {
            String error = spelValidationService.getSyntaxError(request.getSpel());
            return RuleValidateResponse.builder()
                    .success(false)
                    .errorMessage("SpEL语法错误: " + error)
                    .logs(List.of("语法检查失败: " + error))
                    .build();
        }
        
        // 2. 沙箱执行
        return spelValidationService.executeInSandbox(request.getSpel(), request.getMockData());
    }
    
    /**
     * 保存规则
     */
    @Transactional
    public RuleNode saveRule(String productId, String name, String naturalLanguage, 
                            String spel, String explanation, Double confidence) {
        
        // 生成规则ID
        String ruleId = "RULE_" + System.currentTimeMillis();
        
        // 查找是否已有同名规则
        Optional<RuleNode> existing = ruleRepository.findLatestByProductId(productId);
        int version = existing.map(r -> r.getVersion() + 1).orElse(1);
        
        RuleNode rule = RuleNode.builder()
                .ruleId(ruleId)
                .name(name)
                .productId(productId)
                .naturalLanguage(naturalLanguage)
                .spelExpression(spel)
                .explanation(explanation)
                .confidence(confidence)
                .status("DRAFT")
                .version(version)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return ruleRepository.save(rule);
    }
    
    /**
     * 获取产品的所有规则
     */
    @Transactional(readOnly = true)
    public List<RuleNode> getRulesByProductId(String productId) {
        return ruleRepository.findByProductId(productId);
    }
    
    /**
     * 发布规则
     */
    @Transactional
    public RuleNode publishRule(String ruleId) {
        RuleNode rule = ruleRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new RuntimeException("规则不存在: " + ruleId));
        
        rule.setStatus("PUBLISHED");
        rule.setUpdatedAt(LocalDateTime.now());
        
        return ruleRepository.save(rule);
    }
    
    /**
     * 构建本体上下文
     */
    private String buildOntologyContext(List<MetadataNode> metadataList, List<EnumValueNode> enumValues) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("| 语义名称 | SpEL变量 | 物理路径 | 数据类型 |\n");
        sb.append("|---------|---------|---------|--------|\n");
        for (MetadataNode meta : metadataList) {
            sb.append(String.format("| %s | #%s | %s | %s |\n",
                    meta.getName(), meta.getMetaId(), meta.getPath(), meta.getType()));
        }
        
        sb.append("\n| 枚举值 | 业务含义 | 所属属性 |\n");
        sb.append("|-------|---------|--------|\n");
        for (EnumValueNode ev : enumValues) {
            sb.append(String.format("| %s | %s | %s |\n",
                    ev.getValue(), ev.getMeaning(), ev.getAttributeId()));
        }
        
        return sb.toString();
    }
    
    /**
     * 解析LLM响应
     */
    private RuleGenerateResponse parseLlmResponse(String llmResponse, List<MetadataNode> metadataList) {
        try {
            // 尝试提取JSON
            String json = extractJson(llmResponse);
            JSONObject obj = JSON.parseObject(json);
            
            List<RuleGenerateResponse.SemanticTraceItem> trace = new ArrayList<>();
            if (obj.containsKey("usedFields")) {
                List<String> usedFields = obj.getList("usedFields", String.class);
                for (String field : usedFields) {
                    MetadataNode meta = metadataList.stream()
                            .filter(m -> m.getMetaId().equals(field))
                            .findFirst()
                            .orElse(null);
                    if (meta != null) {
                        trace.add(RuleGenerateResponse.SemanticTraceItem.builder()
                                .nlTerm(meta.getName())
                                .mappedField(meta.getMetaId())
                                .physicalPath(meta.getPath())
                                .confidence(meta.getConfidence())
                                .build());
                    }
                }
            }
            
            return RuleGenerateResponse.builder()
                    .spel(obj.getString("spel"))
                    .explanation(obj.getString("explanation"))
                    .confidence(obj.getDoubleValue("confidence"))
                    .evidenceNodes(obj.getList("usedFields", String.class))
                    .semanticTrace(trace)
                    .build();
                    
        } catch (Exception e) {
            log.error("解析LLM响应失败", e);
            return RuleGenerateResponse.builder()
                    .spel("true")
                    .explanation("解析失败，返回默认表达式")
                    .confidence(0.0)
                    .evidenceNodes(Collections.emptyList())
                    .semanticTrace(Collections.emptyList())
                    .build();
        }
    }
    
    /**
     * 从响应中提取JSON
     */
    private String extractJson(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }
}
