package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 规则验证请求 DTO
 */
@Data
@Schema(description = "规则验证请求")
public class RuleValidateRequest {

    @Schema(description = "SpEL 表达式", example = "#businessTypeCode == '3' ? #soId == '2831' : true")
    @NotBlank(message = "SpEL表达式不能为空")
    private String spel;

    @Schema(description = "Mock 数据")
    private Map<String, Object> mockData;
}
