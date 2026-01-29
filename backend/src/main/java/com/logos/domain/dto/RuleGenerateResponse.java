package com.logos.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 规则生成响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleGenerateResponse {
    
    /**
     * 生成的SpEL表达式
     */
    private String spel;
    
    /**
     * 解释说明
     */
    private String explanation;
    
    /**
     * 置信度（0-1）
     */
    private Double confidence;
    
    /**
     * 证据节点（命中的图谱节点）
     */
    private List<String> evidenceNodes;
    
    /**
     * 语义追踪信息
     */
    private List<SemanticTraceItem> semanticTrace;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemanticTraceItem {
        private String nlTerm;        // 自然语言词汇
        private String mappedField;   // 映射字段
        private String physicalPath;  // 物理路径
        private Double confidence;    // 置信度
    }
}
