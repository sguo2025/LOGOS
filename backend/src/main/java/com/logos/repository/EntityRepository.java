package com.logos.repository;

import com.logos.domain.entity.EntityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 实体节点 Repository
 */
@Repository
public interface EntityRepository extends Neo4jRepository<EntityNode, String> {

    Optional<EntityNode> findByCode(String code);

    @Query("MATCH (e:Entity {code: $code}) RETURN e")
    Optional<EntityNode> findEntityByCode(String code);
}
