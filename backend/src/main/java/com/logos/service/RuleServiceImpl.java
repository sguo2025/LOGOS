package com.logos.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.logos.domain.dto.RuleGenerateRequest;
import com.logos.domain.dto.RuleGenerateResponse;
import com.logos.domain.dto.RuleValidateRequest;
import com.logos.domain.dto.RuleValidateResponse;
import com.logos.domain.entity.BusinessConstraintNode;
import com.logos.domain.entity.MetadataNode;
import com.logos.engine.spel.SpelExecutionEngine;
import com.logos.engine.spel.SpelExecutionResult;
import com.logos.llm.LlmClient;
import com.logos.llm.PromptTemplates;
import com.logos.repository.BusinessConstraintRepository;
import com.logos.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final LlmClient llmClient;
    private final SpelExecutionEngine spelEngine;
    private final MetadataRepository metadataRepository;
    private final BusinessConstraintRepository constraintRepository;

    @Override
    public RuleGenerateResponse generate(RuleGenerateRequest request) {
        log.info("开始生成规则，产品ID: {}, 自然语言: {}", request.getProductId(), request.getNaturalLanguage());

        try {
            // 1. 从 Neo4j 获取本体上下文
            String ontologyContext = buildOntologyContext(request.getProductId());
            log.debug("本体上下文: {}", ontologyContext);

            // 2. 构建 LLM 请求
            String userMessage = PromptTemplates.buildNl2SpelUserMessage(
                    request.getNaturalLanguage(), ontologyContext);

            // 3. 调用 LLM 生成 SpEL
            String llmResponse = llmClient.chat(PromptTemplates.NL2SPEL_SYSTEM, userMessage);
            log.debug("LLM 响应: {}", llmResponse);

            // 4. 解析 LLM 响应
            return parseLlmResponse(llmResponse);
        } catch (Exception e) {
            log.error("规则生成失败", e);
            return RuleGenerateResponse.builder()
                    .spel("")
                    .explanation("生成失败: " + e.getMessage())
                    .confidence(0.0)
                    .evidenceNodes(List.of())
                    .build();
        }
    }

    @Override
    public RuleValidateResponse validate(RuleValidateRequest request) {
        log.info("开始验证规则，SpEL: {}", request.getSpel());

        // 执行 SpEL
        SpelExecutionResult result = spelEngine.execute(request.getSpel(), request.getMockData());

        return RuleValidateResponse.builder()
                .success(result.isSuccess())
                .actualValue(result.getResult())
                .logs(result.getLogs())
                .build();
    }

    /**
     * 构建本体上下文
     */
    private String buildOntologyContext(String productId) {
        Map<String, Object> context = new HashMap<>();

        // 获取元数据
        List<MetadataNode> metadataList = new ArrayList<>();
        metadataList.addAll(metadataRepository.findByEntityCode("RuleContext"));
        metadataList.addAll(metadataRepository.findByEntityCode("ProdInst"));

        Map<String, String> metadataMap = new HashMap<>();
        for (MetadataNode m : metadataList) {
            metadataMap.put(m.getId(), m.getName() + " (路径: " + m.getPath() + ")");
        }
        context.put("metadata", metadataMap);

        // 获取业务约束
        List<BusinessConstraintNode> constraints = constraintRepository.findByProductId(productId);
        context.put("constraints", constraints);

        return JSON.toJSONString(context);
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
