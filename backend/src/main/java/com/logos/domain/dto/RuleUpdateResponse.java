package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则更新/热发布响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "规则更新响应")
public class RuleUpdateResponse {

    @Schema(description = "规则ID", example = "RULE_LX_001")
    private String ruleId;

    @Schema(description = "规则版本号", example = "v1.0.1")
    private String version;

    @Schema(description = "发布状态", example = "PUBLISHED")
    private String status;

    @Schema(description = "更新时间戳", example = "2026-01-30T10:30:00Z")
    private String updatedAt;

    @Schema(description = "操作说明", example = "规则已成功热发布到缓存")
    private String message;

    @Schema(description = "推理溯源路径")
    private String trace;
}
