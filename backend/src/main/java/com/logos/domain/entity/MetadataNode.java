package com.logos.domain.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * 元数据节点 - 物理属性映射
 */
@Data
@Node("Metadata")
public class MetadataNode {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("path")
    private String path;

    @Property("type")
    private String type;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private EntityNode belongsTo;
}
