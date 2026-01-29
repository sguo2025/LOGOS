package com.logos.domain.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 动作节点 - 原子能力定义
 */
@Data
@Node("Action")
public class ActionNode {

    @Id
    private String code;

    @Property("handler")
    private String handler;

    @Property("description")
    private String description;
}
