package com.logos.repository;

import com.logos.domain.entity.BusinessConstraintNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 业务约束节点仓储
 */
@Repository
public interface BusinessConstraintRepository extends Neo4jRepository<BusinessConstraintNode, Long> {
    
    /**
     * 根据约束ID查找
     */
    Optional<BusinessConstraintNode> findByConstraintId(String constraintId);
    
    /**
     * 根据产品ID查找所有约束
     */
    @Query("MATCH (c:BusinessConstraint {targetProductId: $prodId}) WHERE c.enabled = true RETURN c")
    List<BusinessConstraintNode> findByProductId(@Param("prodId") String prodId);
    
    /**
     * 根据产品ID和业务类型查找约束
     */
    @Query("""
        MATCH (c:BusinessConstraint)
        WHERE c.targetProductId = $prodId 
          AND c.targetBusinessType = $businessType
          AND c.enabled = true
        RETURN c
    """)
    List<BusinessConstraintNode> findByProductIdAndBusinessType(
            @Param("prodId") String prodId,
            @Param("businessType") String businessType);
    
    /**
     * 获取所有启用的约束
     */
    @Query("MATCH (c:BusinessConstraint) WHERE c.enabled = true RETURN c")
    List<BusinessConstraintNode> findAllEnabled();
}
