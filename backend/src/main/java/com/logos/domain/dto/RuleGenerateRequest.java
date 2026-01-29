package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 规则生成请求 DTO
 */
@Data
@Schema(description = "规则生成请求")
public class RuleGenerateRequest {

    @Schema(description = "产品ID", example = "80000122")
    @NotBlank(message = "产品ID不能为空")
    private String productId;

    @Schema(description = "自然语言描述", example = "当业务类型是融合光网时，只准做拆机操作")
    @NotBlank(message = "自然语言描述不能为空")
    private String naturalLanguage;

    @Schema(description = "上下文类型", example = "AccessProdInst")
    private String context;
}
