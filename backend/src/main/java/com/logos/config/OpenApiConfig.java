package com.logos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI配置
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI logosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LOGOS Rule Engine API")
                        .description("基于本体建模与大模型的语义规则编译器接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LOGOS Team")
                                .email("logos@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("开发环境"),
                        new Server().url("http://api.logos-system.com").description("生产环境")
                ));
    }
}
