package com.logos.engine.spel;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * LOGOS自定义SpEL执行上下文
 * 预加载规则执行所需的根对象和工具函数
 */
public class LogosEvaluationContext extends StandardEvaluationContext {
    
    public LogosEvaluationContext(Map<String, Object> variables) {
        super();
        
        // 设置变量
        if (variables != null) {
            variables.forEach(this::setVariable);
        }
        
        // 注册LOGOS工具函数
        registerFunctions();
    }
    
    /**
     * 注册自定义函数
     */
    private void registerFunctions() {
        try {
            // 注册 getAttr 函数
            this.registerFunction("getAttr", 
                LogosFunctions.class.getDeclaredMethod("getAttr", String.class, Map.class));
            
            // 注册 inList 函数
            this.registerFunction("inList",
                LogosFunctions.class.getDeclaredMethod("inList", String.class, String.class));
            
            // 注册 matches 函数
            this.registerFunction("matches",
                LogosFunctions.class.getDeclaredMethod("matches", String.class, String.class));
            
            // 注册 isEmpty 函数
            this.registerFunction("isEmpty",
                LogosFunctions.class.getDeclaredMethod("isEmpty", Object.class));
            
            // 注册 isNotEmpty 函数
            this.registerFunction("isNotEmpty",
                LogosFunctions.class.getDeclaredMethod("isNotEmpty", Object.class));
            
            // 注册 equals 函数
            this.registerFunction("eq",
                LogosFunctions.class.getDeclaredMethod("eq", Object.class, Object.class));
                
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to register LOGOS functions", e);
        }
    }
}
