package com.logos.repository;

import com.logos.domain.entity.EntityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 实体节点仓储
 */
@Repository
public interface EntityRepository extends Neo4jRepository<EntityNode, Long> {
    
    /**
     * 根据实体编码查找
     */
    Optional<EntityNode> findByCode(String code);
    
    /**
     * 获取所有实体及其元数据
     */
    @Query("""
        MATCH (e:Entity)
        OPTIONAL MATCH (e)-[:HAS_METADATA]->(m:Metadata)
        RETURN e, collect(m) as metadataList
    """)
    List<EntityNode> findAllWithMetadata();
    
    /**
     * 根据名称模糊查找
     */
    @Query("MATCH (e:Entity) WHERE e.name CONTAINS $keyword RETURN e")
    List<EntityNode> findByNameContaining(@Param("keyword") String keyword);
}
