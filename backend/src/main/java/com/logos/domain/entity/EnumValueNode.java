package com.logos.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.*;

/**
 * 枚举值节点 - 存储字段的枚举值映射
 */
@Node("EnumValue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumValueNode {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 原始值（如：3, 2831）
     */
    @Property("value")
    private String value;
    
    /**
     * 业务含义（如：融合光网, 拆机）
     */
    @Property("meaning")
    private String meaning;
    
    /**
     * 所属属性ID（如：businessTypeCode）
     */
    @Property("attributeId")
    private String attributeId;
    
    /**
     * 置信度
     */
    @Property("confidence")
    private Double confidence;
}
