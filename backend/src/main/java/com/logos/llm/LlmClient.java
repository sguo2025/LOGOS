package com.logos.llm;

/**
 * LLM 客户端接口
 */
public interface LlmClient {

    /**
     * 发送聊天请求
     *
     * @param prompt 提示词
     * @return 响应内容
     */
    String chat(String prompt);

    /**
     * 发送聊天请求（带系统消息）
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @return 响应内容
     */
    String chat(String systemMessage, String userMessage);
}
