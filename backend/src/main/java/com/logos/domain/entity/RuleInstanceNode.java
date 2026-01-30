package com.logos.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 规则实例节点
 * 对齐规范 - 规则实例与版本管理
 */
@Node("RuleInstance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleInstanceNode {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("spel")
    private String spel;

    @Property("priority")
    private Integer priority;

    @Property("category")
    private String category;

    @Property("version")
    private String version;

    @Property("status")
    private String status;

    @Property("errorMessage")
    private String errorMessage;

    @Property("targetProductId")
    private String targetProductId;

    @Property("createdAt")
    private String createdAt;

    @Property("updatedAt")
    private String updatedAt;
}
