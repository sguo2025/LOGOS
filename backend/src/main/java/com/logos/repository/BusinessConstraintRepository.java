package com.logos.repository;

import com.logos.domain.entity.BusinessConstraintNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 业务约束节点 Repository
 */
@Repository
public interface BusinessConstraintRepository extends Neo4jRepository<BusinessConstraintNode, String> {

    @Query("MATCH (bc:BusinessConstraint {targetProductId: $productId}) RETURN bc")
    List<BusinessConstraintNode> findByProductId(String productId);

    @Query("MATCH (bc:BusinessConstraint {targetProductId: $productId, targetBusinessType: $businessType}) RETURN bc")
    Optional<BusinessConstraintNode> findByProductIdAndBusinessType(String productId, String businessType);

    @Query("MATCH (bc:BusinessConstraint) RETURN bc")
    List<BusinessConstraintNode> findAllConstraints();
}
