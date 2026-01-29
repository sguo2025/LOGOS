package com.logos.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Neo4j 数据库配置
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.logos.repository")
@EnableTransactionManagement
public class Neo4jConfig {

    @Bean
    public Neo4jTransactionManager transactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}
