package com.logos.repository;

import com.logos.domain.entity.EnumValueNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 枚举值节点仓储
 */
@Repository
public interface EnumValueRepository extends Neo4jRepository<EnumValueNode, Long> {
    
    /**
     * 根据属性ID查找所有枚举值
     */
    List<EnumValueNode> findByAttributeId(String attributeId);
    
    /**
     * 根据值和属性ID查找
     */
    Optional<EnumValueNode> findByValueAndAttributeId(String value, String attributeId);
    
    /**
     * 根据业务含义模糊查找
     */
    @Query("MATCH (e:EnumValue) WHERE e.meaning CONTAINS $keyword RETURN e")
    List<EnumValueNode> findByMeaningContaining(@Param("keyword") String keyword);
    
    /**
     * 获取所有枚举值用于RAG检索
     */
    @Query("MATCH (e:EnumValue) RETURN e ORDER BY e.attributeId, e.value")
    List<EnumValueNode> findAllForRag();
}
