package com.logos.repository;

import com.logos.domain.entity.MetadataNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据节点 Repository
 */
@Repository
public interface MetadataRepository extends Neo4jRepository<MetadataNode, String> {

    @Query("MATCH (m:Metadata)-[:BELONGS_TO]->(e:Entity {code: $entityCode}) RETURN m")
    List<MetadataNode> findByEntityCode(String entityCode);

    @Query("MATCH (m:Metadata {id: $id}) RETURN m")
    MetadataNode findMetadataById(String id);

    @Query("MATCH (m:Metadata) WHERE m.name CONTAINS $keyword OR m.id CONTAINS $keyword RETURN m")
    List<MetadataNode> searchByKeyword(String keyword);
}
