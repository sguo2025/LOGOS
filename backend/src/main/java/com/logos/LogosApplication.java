package com.logos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * LOGOS - Logic Ontology Generation & Operation System
 * 
 * 智能规则中台：基于本体建模与大模型的业务规则进化系统
 */
@SpringBootApplication
@EnableNeo4jRepositories
public class LogosApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogosApplication.class, args);
    }
}
