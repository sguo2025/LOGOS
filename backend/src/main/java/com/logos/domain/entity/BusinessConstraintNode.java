package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

/**
 * 业务约束节点 - 存储业务逻辑约束条件
 */
@Node("BusinessConstraint")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessConstraintNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 约束唯一标识（如：BC_LX_001）
     */
    @Property("constraintId")
    private String constraintId;
    
    /**
     * 约束名称（如：灵犀融合光网约束）
     */
    @Property("name")
    private String name;
    
    /**
     * 目标产品ID
     */
    @Property("targetProductId")
    private String targetProductId;
    
    /**
     * 目标业务类型
     */
    @Property("targetBusinessType")
    private String targetBusinessType;
    
    /**
     * 允许的操作（逗号分隔，如：2831）
     */
    @Property("allowedActions")
    private String allowedActions;
    
    /**
     * 豁免的操作类型（JSON数组格式，如：["1100", "1200"]）
     */
    @Property("exemptOperTypes")
    private String exemptOperTypes;
    
    /**
     * 错误消息
     */
    @Property("errorMessage")
    private String errorMessage;
    
    /**
     * 约束是否启用
     */
    @Property("enabled")
    private Boolean enabled;
}
