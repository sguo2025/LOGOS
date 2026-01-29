package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

/**
 * 规则节点 - 存储生成的SpEL规则
 */
@Node("Rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 规则唯一标识
     */
    @Property("ruleId")
    private String ruleId;
    
    /**
     * 规则名称
     */
    @Property("name")
    private String name;
    
    /**
     * 规则描述
     */
    @Property("description")
    private String description;
    
    /**
     * 原始自然语言输入
     */
    @Property("naturalLanguage")
    private String naturalLanguage;
    
    /**
     * 生成的SpEL表达式
     */
    @Property("spelExpression")
    private String spelExpression;
    
    /**
     * 解释说明
     */
    @Property("explanation")
    private String explanation;
    
    /**
     * 关联的产品ID
     */
    @Property("productId")
    private String productId;
    
    /**
     * 置信度
     */
    @Property("confidence")
    private Double confidence;
    
    /**
     * 规则状态（DRAFT/TESTING/PUBLISHED/ARCHIVED）
     */
    @Property("status")
    private String status;
    
    /**
     * 版本号
     */
    @Property("version")
    private Integer version;
    
    /**
     * 创建时间
     */
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Property("updatedAt")
    private LocalDateTime updatedAt;
    
    /**
     * 创建者
     */
    @Property("createdBy")
    private String createdBy;
}
