package com.logos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * SpEL 引擎配置
 */
@Configuration
public class SpelConfig {

    @Bean
    public ExpressionParser spelExpressionParser() {
        return new SpelExpressionParser();
    }
}
