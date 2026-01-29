package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

/**
 * 产品节点 - 代表具体的业务产品
 */
@Node("Product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 产品ID（如：80000122）
     */
    @Property("prodId")
    private String prodId;
    
    /**
     * 产品名称（如：灵犀专线）
     */
    @Property("name")
    private String name;
    
    /**
     * 产品描述
     */
    @Property("desc")
    private String description;
    
    /**
     * 产品关联的元数据
     */
    @Relationship(type = "HAS_METADATA", direction = Relationship.Direction.OUTGOING)
    private List<MetadataNode> metadataList;
    
    /**
     * 产品关联的业务约束
     */
    @Relationship(type = "HAS_CONSTRAINT", direction = Relationship.Direction.OUTGOING)
    private List<BusinessConstraintNode> constraints;
}
