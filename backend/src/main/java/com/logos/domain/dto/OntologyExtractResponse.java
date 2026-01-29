package com.logos.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 本体提取响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OntologyExtractResponse {
    
    /**
     * 提取的节点列表
     */
    private List<ExtractedNode> nodes;
    
    /**
     * 提取的关系列表
     */
    private List<ExtractedRelation> relations;
    
    /**
     * 提取的逻辑片段
     */
    private List<ExtractedLogic> logicFragments;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedNode {
        private String id;
        private String name;
        private String type;          // Metadata/Product/EnumValue
        private String path;
        private Double confidence;
        private String evidence;      // 证据（代码行）
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedRelation {
        private String from;
        private String to;
        private String type;          // HAS_PROPERTY/BELONGS_TO等
        private Double confidence;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedLogic {
        private String pattern;       // 逻辑模式
        private String explanation;   // 业务解释
        private List<String> usedFields;
        private Double confidence;
    }
}
