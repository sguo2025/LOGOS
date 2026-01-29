package com.logos.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/**
 * 规则验证请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleValidateRequest {
    
    /**
     * SpEL表达式
     */
    @NotBlank(message = "SpEL表达式不能为空")
    private String spel;
    
    /**
     * Mock数据
     */
    private Map<String, Object> mockData;
}
