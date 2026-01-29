package com.logos.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * 规则生成请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleGenerateRequest {
    
    /**
     * 产品ID（如：80000122）
     */
    @NotBlank(message = "产品ID不能为空")
    private String productId;
    
    /**
     * 自然语言描述
     */
    @NotBlank(message = "自然语言描述不能为空")
    private String naturalLanguage;
    
    /**
     * 上下文类型（如：AccessProdInst）
     */
    private String contextType;
}
