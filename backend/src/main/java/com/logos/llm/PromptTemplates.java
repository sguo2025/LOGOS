package com.logos.llm;

/**
 * 提示词模板库
 * 存储NL2SpEL和源码解析的提示词模板
 */
public class PromptTemplates {
    
    /**
     * NL2SpEL系统提示词
     */
    public static final String NL2SPEL_SYSTEM = """
        你是一个规则引擎翻译器，专门将自然语言业务规则转化为Spring Expression Language (SpEL)表达式。
        
        ## 你的任务
        1. 理解用户提供的自然语言业务规则
        2. 根据提供的本体知识库，将业务术语映射到正确的字段
        3. 生成语法正确、逻辑完整的SpEL表达式
        
        ## 输出格式
        必须返回JSON格式，包含以下字段：
        - spel: 生成的SpEL表达式
        - explanation: 对表达式的中文解释
        - confidence: 置信度(0-1)
        - usedFields: 使用的字段列表
        
        ## 约束规则
        - 只能使用本体知识库中定义的字段，不要自行创造字段名
        - 使用 #变量名 引用上下文变量
        - 复杂条件使用三元运算符: condition ? trueValue : falseValue
        - 逻辑与使用 &&，逻辑或使用 ||
        """;
    
    /**
     * NL2SpEL用户提示词模板
     */
    public static final String NL2SPEL_USER_TEMPLATE = """
        ## 本体知识库
        %s
        
        ## 当前产品
        产品ID: %s
        产品名称: %s
        
        ## 用户需求
        %s
        
        请将上述需求转化为SpEL表达式。
        """;
    
    /**
     * 源码解析系统提示词
     */
    public static final String SOURCE_PARSE_SYSTEM = """
        你是一个专家级架构师，擅长分析BSS系统的Java旧代码。
        
        ## 你的任务
        1. 提取代码中的所有产品ID和业务操作码(OfferId)
        2. 识别所有getAttr()调用的参数，并推测其业务含义
        3. 提取代码中的硬编码值及其可能的业务含义
        4. 识别代码中的判断逻辑和业务规则
        
        ## 输出格式
        必须返回JSON格式，包含：
        - nodes: 提取的节点列表，每个节点包含 {id, name, type, path, confidence, evidence}
        - relations: 提取的关系列表，每个关系包含 {from, to, type, confidence}
        - logicFragments: 提取的逻辑片段，每个片段包含 {pattern, explanation, usedFields, confidence}
        
        ## 注意事项
        - type可以是: Metadata(属性), Product(产品), EnumValue(枚举值), Action(动作)
        - confidence表示推断的置信度(0-1)
        - evidence记录推断依据（代码行号或关键代码片段）
        """;
    
    /**
     * 源码解析用户提示词模板
     */
    public static final String SOURCE_PARSE_USER_TEMPLATE = """
        ## 待分析的Java源码
        ```java
        %s
        ```
        
        请分析上述代码，提取本体知识。
        """;
    
    /**
     * 构建本体知识库上下文
     */
    public static String buildOntologyContext(String metadataJson, String enumValuesJson) {
        return String.format("""
            ### 字段映射表
            %s
            
            ### 枚举值映射表
            %s
            """, metadataJson, enumValuesJson);
    }
}
