package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

/**
 * 实体节点 - 代表业务领域中的核心概念
 * 如：RuleContext（规则上下文）、ProdInst（产品实例）、BusinessConstraint（业务约束）
 */
@Node("Entity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("code")
    private String code;
    
    @Property("name")
    private String name;
    
    @Property("desc")
    private String description;
    
    /**
     * 该实体拥有的属性元数据
     */
    @Relationship(type = "HAS_METADATA", direction = Relationship.Direction.OUTGOING)
    private List<MetadataNode> metadataList;
}
