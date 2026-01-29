package com.logos.repository;

import com.logos.domain.entity.ActionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 动作节点 Repository
 */
@Repository
public interface ActionRepository extends Neo4jRepository<ActionNode, String> {

    @Query("MATCH (a:Action {code: $code}) RETURN a")
    Optional<ActionNode> findByCode(String code);
}
