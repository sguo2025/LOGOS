package com.logos.repository;

import com.logos.domain.entity.RuleInstanceNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 规则实例仓库
 */
@Repository
public interface RuleInstanceRepository extends Neo4jRepository<RuleInstanceNode, String> {

    /**
     * 根据产品ID查找规则
     */
    List<RuleInstanceNode> findByTargetProductId(String productId);

    /**
     * 查找已发布的规则
     */
    List<RuleInstanceNode> findByStatus(String status);

    /**
     * 根据分类查找规则
     */
    List<RuleInstanceNode> findByCategory(String category);

    /**
     * 根据优先级排序获取规则
     */
    @Query("MATCH (r:RuleInstance) WHERE r.targetProductId = $productId AND r.status = 'PUBLISHED' RETURN r ORDER BY r.priority ASC")
    List<RuleInstanceNode> findPublishedRulesByProductIdOrderByPriority(String productId);

    /**
     * 更新规则 SpEL 和版本
     */
    @Query("MATCH (r:RuleInstance {id: $ruleId}) SET r.spel = $spel, r.version = $version, r.updatedAt = $updatedAt, r.status = $status RETURN r")
    Optional<RuleInstanceNode> updateRule(String ruleId, String spel, String version, String updatedAt, String status);
}
