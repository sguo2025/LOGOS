package com.logos.domain.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 实体节点 - 核心概念节点
 */
@Data
@Node("Entity")
public class EntityNode {

    @Id
    private String code;

    @Property("name")
    private String name;

    @Property("desc")
    private String description;
}
