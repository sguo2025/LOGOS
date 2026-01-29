package com.logos.engine.spel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SpEL 执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpelExecutionResult {

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 执行结果
     */
    private Object result;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 执行日志
     */
    private List<String> logs;
}
