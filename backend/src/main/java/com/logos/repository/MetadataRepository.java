package com.logos.repository;

import com.logos.domain.entity.MetadataNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 元数据节点仓储
 */
@Repository
public interface MetadataRepository extends Neo4jRepository<MetadataNode, Long> {
    
    /**
     * 根据元数据ID查找
     */
    Optional<MetadataNode> findByMetaId(String metaId);
    
    /**
     * 根据产品ID查找所有元数据
     */
    @Query("MATCH (m:Metadata)-[:BELONGS_TO]->(p:Product {prodId: $prodId}) RETURN m")
    List<MetadataNode> findByProductId(@Param("prodId") String prodId);
    
    /**
     * 根据名称模糊查找
     */
    @Query("MATCH (m:Metadata) WHERE m.name CONTAINS $keyword RETURN m")
    List<MetadataNode> findByNameContaining(@Param("keyword") String keyword);
    
    /**
     * 根据物理路径查找
     */
    Optional<MetadataNode> findByPath(String path);
    
    /**
     * 获取所有元数据用于RAG检索
     */
    @Query("MATCH (m:Metadata) RETURN m ORDER BY m.metaId")
    List<MetadataNode> findAllForRag();
}
