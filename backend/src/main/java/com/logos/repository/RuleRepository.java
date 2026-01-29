package com.logos.repository;

import com.logos.domain.entity.RuleNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 规则节点仓储
 */
@Repository
public interface RuleRepository extends Neo4jRepository<RuleNode, Long> {
    
    /**
     * 根据规则ID查找
     */
    Optional<RuleNode> findByRuleId(String ruleId);
    
    /**
     * 根据产品ID查找所有规则
     */
    List<RuleNode> findByProductId(String productId);
    
    /**
     * 根据状态查找规则
     */
    List<RuleNode> findByStatus(String status);
    
    /**
     * 根据产品ID和状态查找
     */
    List<RuleNode> findByProductIdAndStatus(String productId, String status);
    
    /**
     * 获取产品下最新版本的规则
     */
    @Query("""
        MATCH (r:Rule {productId: $productId})
        RETURN r ORDER BY r.version DESC LIMIT 1
    """)
    Optional<RuleNode> findLatestByProductId(@Param("productId") String productId);
    
    /**
     * 获取所有已发布的规则
     */
    @Query("MATCH (r:Rule {status: 'PUBLISHED'}) RETURN r ORDER BY r.updatedAt DESC")
    List<RuleNode> findAllPublished();
}
