package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

/**
 * 元数据节点 - 存储物理字段与业务含义的映射
 * 将物理代码中的属性提取为语义化标签
 */
@Node("Metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 元数据唯一标识（如：businessTypeCode, soId, operType）
     */
    @Property("metaId")
    private String metaId;
    
    /**
     * 业务含义名称（如：业务类型编码、服务提供ID）
     */
    @Property("name")
    private String name;
    
    /**
     * 物理路径（如：COL1, orderRequest.serviceOfferId）
     */
    @Property("path")
    private String path;
    
    /**
     * 数据类型（如：String, Integer, Boolean）
     */
    @Property("type")
    private String type;
    
    /**
     * 来源描述（Java代码/人工确认/历史规则）
     */
    @Property("source")
    private String source;
    
    /**
     * 置信度（0-1）
     */
    @Property("confidence")
    private Double confidence;
}
