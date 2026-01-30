package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 规则生成响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "规则生成响应")
public class RuleGenerateResponse {

    @Schema(description = "生成的 SpEL 表达式", example = "#businessTypeCode == '3' ? #soId == '2831' : true")
    private String spel;

    @Schema(description = "解释说明", example = "识别到业务类型映射为COL1...")
    private String explanation;

    @Schema(description = "置信度", example = "0.95")
    private Double confidence;

    @Schema(description = "证据节点列表")
    private List<String> evidenceNodes;

    @Schema(description = "推理溯源路径", example = "业务需求(融合光网) -> 命中实体(ProdInst:80000122) -> 匹配属性(COL1) -> 常量值(3)")
    private String trace;
}
