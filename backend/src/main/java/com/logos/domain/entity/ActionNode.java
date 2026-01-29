package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

/**
 * 动作节点 - 代表原子能力/操作
 */
@Node("Action")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 动作编码（如：ShouldSkipCheck, ValidateConstraint, BlockExecution）
     */
    @Property("code")
    private String code;
    
    /**
     * 动作名称
     */
    @Property("name")
    private String name;
    
    /**
     * 处理器类名/方法签名（如：LogosUtils.shouldSkip）
     */
    @Property("handler")
    private String handler;
    
    /**
     * 动作描述
     */
    @Property("desc")
    private String description;
}
