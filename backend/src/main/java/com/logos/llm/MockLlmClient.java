package com.logos.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Mock LLM客户端
 * 用于开发测试环境，不依赖真实LLM服务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "logos.llm.provider", havingValue = "mock")
public class MockLlmClient implements LlmClient {
    
    @Override
    public String chat(String prompt) {
        log.info("Mock LLM收到请求: {}", prompt.substring(0, Math.min(100, prompt.length())));
        
        // 简单的模式匹配，返回模拟响应
        if (prompt.contains("融合光网") || prompt.contains("拆机")) {
            return """
                {
                    "spel": "#businessTypeCode == '3' ? #serviceOfferId == '2831' : true",
                    "explanation": "识别到业务类型为COL1，融合光网值为3，拆机操作为2831。生成的表达式含义：当业务类型为融合光网时，只允许拆机操作。",
                    "confidence": 0.95,
                    "usedFields": ["businessTypeCode", "serviceOfferId"]
                }
                """;
        }
        
        return """
            {
                "spel": "true",
                "explanation": "无法识别具体的业务规则，返回默认通过表达式。",
                "confidence": 0.5,
                "usedFields": []
            }
            """;
    }
    
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        log.info("Mock LLM系统提示: {}", systemPrompt.substring(0, Math.min(50, systemPrompt.length())));
        return chat(userPrompt);
    }
    
    @Override
    public String getProviderName() {
        return "Mock";
    }
}
