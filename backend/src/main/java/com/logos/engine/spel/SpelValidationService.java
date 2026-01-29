package com.logos.engine.spel;

import com.logos.domain.dto.RuleValidateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * SpEL表达式验证服务
 * 提供SpEL语法校验和沙箱执行功能
 */
@Slf4j
@Service
public class SpelValidationService {
    
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Value("${logos.spel.sandbox.timeout:5000}")
    private long sandboxTimeout;
    
    /**
     * 验证SpEL语法
     */
    public boolean validateSyntax(String spel) {
        try {
            parser.parseExpression(spel);
            return true;
        } catch (Exception e) {
            log.error("SpEL语法错误: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取语法错误信息
     */
    public String getSyntaxError(String spel) {
        try {
            parser.parseExpression(spel);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    /**
     * 在沙箱中执行SpEL
     */
    public RuleValidateResponse executeInSandbox(String spel, Map<String, Object> context) {
        List<String> logs = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 解析表达式
            logs.add("正在解析SpEL表达式...");
            Expression expression = parser.parseExpression(spel);
            
            // 构建执行上下文
            logs.add("构建执行上下文...");
            LogosEvaluationContext evalContext = new LogosEvaluationContext(context);
            
            // 记录上下文变量
            context.forEach((k, v) -> logs.add(String.format("设置变量 %s = %s", k, v)));
            
            // 在超时限制内执行
            logs.add("开始执行表达式...");
            Future<Object> future = executor.submit(() -> expression.getValue(evalContext));
            
            Object result = future.get(sandboxTimeout, TimeUnit.MILLISECONDS);
            
            long executionTime = System.currentTimeMillis() - startTime;
            logs.add(String.format("执行完成，结果: %s，耗时: %dms", result, executionTime));
            
            return RuleValidateResponse.builder()
                    .success(true)
                    .result(result)
                    .logs(logs)
                    .executionTime(executionTime)
                    .build();
                    
        } catch (TimeoutException e) {
            logs.add("执行超时: " + sandboxTimeout + "ms");
            return RuleValidateResponse.builder()
                    .success(false)
                    .errorMessage("SpEL执行超时")
                    .logs(logs)
                    .executionTime(sandboxTimeout)
                    .build();
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logs.add("执行失败: " + e.getMessage());
            return RuleValidateResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .logs(logs)
                    .executionTime(executionTime)
                    .build();
        }
    }
    
    /**
     * 快速执行SpEL（不带详细日志）
     */
    public Object execute(String spel, Map<String, Object> context) {
        Expression expression = parser.parseExpression(spel);
        LogosEvaluationContext evalContext = new LogosEvaluationContext(context);
        return expression.getValue(evalContext);
    }
}
