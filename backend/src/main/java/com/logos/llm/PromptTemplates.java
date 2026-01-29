package com.logos.llm;

/**
 * 提示词模板
 */
public final class PromptTemplates {

    private PromptTemplates() {
    }

    /**
     * 源码知识提取提示词
     */
    public static final String CODE_EXTRACTION_SYSTEM = """
            你是一个专家级架构师，擅长分析 BSS 系统旧代码。
            你需要从 Java 源代码中提取以下信息：
            1. 提取所有的产品 ID 和业务操作码 (OfferId)
            2. 识别所有 getAttr() 的参数并推测其业务含义
            3. 输出本体五元组 JSON
            
            输出格式必须是严格的 JSON:
            {
                "nodes": [
                    {"id": "字段ID", "name": "业务名称", "type": "Metadata", "path": "物理路径"}
                ],
                "relations": [
                    {"from": "源节点", "to": "目标节点", "type": "关系类型"}
                ]
            }
            """;

    /**
     * NL2SpEL 生成提示词
     */
    public static final String NL2SPEL_SYSTEM = """
            你是一个规则引擎翻译器，负责将自然语言需求转化为 Spring EL 表达式。
            
            规则：
            1. 使用 #变量名 访问上下文变量
            2. 使用 #getAttr('字段名') 获取属性值
            3. 生成的表达式必须逻辑闭环
            4. 只使用提供的元数据中定义的字段
            
            输出格式必须是严格的 JSON:
            {
                "spel": "生成的 SpEL 表达式",
                "explanation": "解释说明",
                "confidence": 0.95,
                "evidenceNodes": ["使用的节点列表"]
            }
            """;

    /**
     * 构建 NL2SpEL 用户消息
     */
    public static String buildNl2SpelUserMessage(String naturalLanguage, String ontologyContext) {
        return String.format("""
                本体知识库: %s
                
                需求: %s
                
                请将上述需求转化为 SpEL 表达式。
                """, ontologyContext, naturalLanguage);
    }

    /**
     * 构建代码提取用户消息
     */
    public static String buildCodeExtractionUserMessage(String javaCode) {
        return String.format("""
                请分析以下 Java 代码并提取本体信息:
                
                ```java
                %s
                ```
                """, javaCode);
    }
}
