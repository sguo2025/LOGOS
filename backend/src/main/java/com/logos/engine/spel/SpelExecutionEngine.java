package com.logos.engine.spel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SpEL 执行引擎
 */
@Slf4j
@Component
public class SpelExecutionEngine {

    private final ExpressionParser parser;

    public SpelExecutionEngine(ExpressionParser parser) {
        this.parser = parser;
    }

    /**
     * 执行 SpEL 表达式
     */
    public SpelExecutionResult execute(String spel, Map<String, Object> context) {
        List<String> logs = new ArrayList<>();
        logs.add("开始执行 SpEL: " + spel);

        try {
            EvaluationContext evalContext = createContext(context, logs);
            Expression expression = parser.parseExpression(spel);
            Object result = expression.getValue(evalContext);
            
            logs.add("执行完成，结果: " + result);
            
            return SpelExecutionResult.builder()
                    .success(true)
                    .result(result)
                    .logs(logs)
                    .build();
        } catch (Exception e) {
            log.error("SpEL 执行失败: {}", e.getMessage(), e);
            logs.add("执行失败: " + e.getMessage());
            
            return SpelExecutionResult.builder()
                    .success(false)
                    .error(e.getMessage())
                    .logs(logs)
                    .build();
        }
    }

    /**
     * 验证 SpEL 语法
     */
    public boolean validate(String spel) {
        try {
            parser.parseExpression(spel);
            return true;
        } catch (Exception e) {
            log.warn("SpEL 语法错误: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建执行上下文
     */
    private EvaluationContext createContext(Map<String, Object> variables, List<String> logs) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // 注册变量
        if (variables != null) {
            variables.forEach((key, value) -> {
                context.setVariable(key, value);
                logs.add("注册变量: " + key + " = " + value);
            });
        }

        // 注册工具函数
        registerFunctions(context, logs);
        
        return context;
    }

    /**
     * 注册工具函数
     */
    private void registerFunctions(StandardEvaluationContext context, List<String> logs) {
        try {
            // 注册 shouldSkip 函数
            context.registerFunction("shouldSkip",
                    LogosUtils.class.getMethod("shouldSkip", String.class, String[].class));
            
            // 注册 inList 函数
            context.registerFunction("inList",
                    LogosUtils.class.getMethod("inList", String.class, String[].class));
            
            // 注册 matches 函数
            context.registerFunction("matches",
                    LogosUtils.class.getMethod("matches", String.class, String.class));
            
            logs.add("已注册工具函数: shouldSkip, inList, matches");
        } catch (NoSuchMethodException e) {
            log.error("注册函数失败", e);
        }
    }
}
