package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 规则执行请求 DTO
 */
@Data
@Schema(description = "规则执行请求")
public class RuleExecuteRequest {

    @Schema(description = "规则ID", example = "RULE_LX_001")
    @NotBlank(message = "规则ID不能为空")
    private String ruleId;

    @Schema(description = "执行上下文数据")
    private Map<String, Object> payload;
}
