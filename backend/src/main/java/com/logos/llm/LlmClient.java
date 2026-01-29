package com.logos.llm;

/**
 * LLM客户端接口
 * 定义大模型调用的统一接口
 */
public interface LlmClient {
    
    /**
     * 发送聊天请求
     * 
     * @param prompt 提示词
     * @return 模型响应
     */
    String chat(String prompt);
    
    /**
     * 发送聊天请求（带系统提示）
     * 
     * @param systemPrompt 系统提示
     * @param userPrompt 用户提示
     * @return 模型响应
     */
    String chat(String systemPrompt, String userPrompt);
    
    /**
     * 获取提供商名称
     */
    String getProviderName();
}
