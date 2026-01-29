package com.logos.repository;

import com.logos.domain.entity.ProductNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 产品节点仓储
 */
@Repository
public interface ProductRepository extends Neo4jRepository<ProductNode, Long> {
    
    /**
     * 根据产品ID查找
     */
    Optional<ProductNode> findByProdId(String prodId);
    
    /**
     * 根据名称模糊查找
     */
    @Query("MATCH (p:Product) WHERE p.name CONTAINS $keyword RETURN p")
    List<ProductNode> findByNameContaining(@Param("keyword") String keyword);
    
    /**
     * 获取产品及其所有元数据
     */
    @Query("""
        MATCH (p:Product {prodId: $prodId})
        OPTIONAL MATCH (p)-[:HAS_METADATA]->(m:Metadata)
        OPTIONAL MATCH (p)-[:HAS_CONSTRAINT]->(c:BusinessConstraint)
        RETURN p, collect(DISTINCT m) as metadataList, collect(DISTINCT c) as constraints
    """)
    Optional<ProductNode> findByProdIdWithDetails(@Param("prodId") String prodId);
    
    /**
     * 获取所有产品列表
     */
    @Query("MATCH (p:Product) RETURN p ORDER BY p.prodId")
    List<ProductNode> findAllProducts();
}
