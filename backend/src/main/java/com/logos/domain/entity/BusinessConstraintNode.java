package com.logos.domain.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 业务约束节点
 */
@Data
@Node("BusinessConstraint")
public class BusinessConstraintNode {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("targetProductId")
    private String targetProductId;

    @Property("targetBusinessType")
    private String targetBusinessType;

    @Property("allowedActions")
    private String allowedActions;

    @Property("exemptOperTypes")
    private String exemptOperTypes;

    @Property("errorMessage")
    private String errorMessage;
}
