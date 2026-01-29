package com.logos.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 规则验证响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleValidateResponse {
    
    /**
     * 验证是否成功
     */
    private Boolean success;
    
    /**
     * 执行结果
     */
    private Object result;
    
    /**
     * 执行日志
     */
    private List<String> logs;
    
    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
    
    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;
}
