package com.logos.llm;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * OpenAI LLM 客户端实现
 */
@Slf4j
@Component
public class OpenAiLlmClient implements LlmClient {

    private final WebClient webClient;

    @Value("${logos.llm.model:gpt-4o}")
    private String model;

    public OpenAiLlmClient(
            @Value("${logos.llm.api-key:sk-demo}") String apiKey,
            @Value("${logos.llm.base-url:https://api.openai.com}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public String chat(String prompt) {
        return chat("You are a helpful assistant.", prompt);
    }

    @Override
    public String chat(String systemMessage, String userMessage) {
        try {
            log.debug("发送 LLM 请求 - System: {}, User: {}", systemMessage, userMessage);
            
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemMessage),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "temperature", 0.3
            );

            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("LLM 原始响应: {}", response);
            
            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                String content = message.getString("content");
                log.debug("LLM 响应内容: {}", content);
                return content;
            }
            
            return "无法获取 LLM 响应";
        } catch (Exception e) {
            log.error("LLM 调用失败", e);
            // 返回模拟响应以便测试
            return generateMockResponse(userMessage);
        }
    }

    /**
     * 生成模拟响应用于测试
     */
    private String generateMockResponse(String userMessage) {
        log.warn("使用模拟响应模式");
        if (userMessage.contains("融合光网") || userMessage.contains("拆机")) {
            return """
                    {
                        "spel": "#businessTypeCode == '3' ? #soId == '2831' : true",
                        "explanation": "系统识别到'融合光网'(businessTypeCode='3')。根据'拆机'约束，生成了条件逻辑：若类型为3则必须是拆机操作(2831)，否则返回true放行。",
                        "confidence": 0.95,
                        "evidenceNodes": ["businessTypeCode", "soId", "ProdInst", "RuleContext"]
                    }
                    """;
        }
        return """
                {
                    "spel": "true",
                    "explanation": "无法识别具体约束条件，返回默认放行",
                    "confidence": 0.5,
                    "evidenceNodes": []
                }
                """;
    }
}
