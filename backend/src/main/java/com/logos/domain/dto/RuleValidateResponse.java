package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 规则验证响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "规则验证响应")
public class RuleValidateResponse {

    @Schema(description = "验证是否成功")
    private Boolean success;

    @Schema(description = "实际执行结果")
    private Object actualValue;

    @Schema(description = "执行日志")
    private List<String> logs;
}
