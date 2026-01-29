package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 本体提取响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "本体提取响应")
public class OntologyExtractResponse {

    @Schema(description = "提取的节点列表")
    private List<ExtractedNode> nodes;

    @Schema(description = "提取的关系列表")
    private List<ExtractedRelation> relations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提取的节点")
    public static class ExtractedNode {
        @Schema(description = "节点ID")
        private String id;
        
        @Schema(description = "节点名称")
        private String name;
        
        @Schema(description = "节点类型")
        private String type;
        
        @Schema(description = "物理路径")
        private String path;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提取的关系")
    public static class ExtractedRelation {
        @Schema(description = "源节点")
        private String from;
        
        @Schema(description = "目标节点")
        private String to;
        
        @Schema(description = "关系类型")
        private String type;
    }
}
