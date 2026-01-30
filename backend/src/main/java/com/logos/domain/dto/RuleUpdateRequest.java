package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 规则更新/热发布请求 DTO
 * 对齐规范 4.4 章节 - 规则热加载
 */
@Data
@Schema(description = "规则更新请求")
public class RuleUpdateRequest {

    @Schema(description = "规则ID", example = "RULE_LX_001")
    @NotBlank(message = "规则ID不能为空")
    private String ruleId;

    @Schema(description = "规则名称", example = "灵犀融合光网准入规则")
    private String name;

    @Schema(description = "SpEL 表达式", example = "#shouldSkip(#operType, '1100', '1200') ? true : (#businessTypeCode == '3' ? #soId == '2831' : true)")
    @NotBlank(message = "SpEL表达式不能为空")
    private String spel;

    @Schema(description = "规则优先级", example = "1")
    private Integer priority;

    @Schema(description = "规则分类", example = "PROD_RULE")
    private String category;

    @Schema(description = "错误消息", example = "灵犀专线业务类型为融合光网时，只允许做拆机操作")
    private String errorMessage;

    @Schema(description = "目标产品ID", example = "80000122")
    private String targetProductId;

    @Schema(description = "是否立即发布", example = "true")
    private Boolean publish;
}
