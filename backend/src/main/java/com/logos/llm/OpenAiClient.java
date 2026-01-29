package com.logos.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OpenAI客户端实现
 * 通过Spring AI集成OpenAI/兼容API
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "logos.llm.provider", havingValue = "openai", matchIfMissing = true)
public class OpenAiClient implements LlmClient {
    
    private final ChatClient.Builder chatClientBuilder;
    
    @Override
    public String chat(String prompt) {
        log.debug("OpenAI请求: {}", prompt.substring(0, Math.min(100, prompt.length())));
        
        ChatClient chatClient = chatClientBuilder.build();
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        log.debug("OpenAI响应: {}", response.substring(0, Math.min(100, response.length())));
        return response;
    }
    
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        log.debug("OpenAI系统提示: {}", systemPrompt.substring(0, Math.min(100, systemPrompt.length())));
        log.debug("OpenAI用户提示: {}", userPrompt.substring(0, Math.min(100, userPrompt.length())));
        
        ChatClient chatClient = chatClientBuilder.build();
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
        
        log.debug("OpenAI响应: {}", response.substring(0, Math.min(100, response.length())));
        return response;
    }
    
    @Override
    public String getProviderName() {
        return "OpenAI";
    }
}
