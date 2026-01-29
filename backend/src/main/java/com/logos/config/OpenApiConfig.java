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
 * OpenAPI 文档配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LOGOS 智能规则引擎 API")
                        .description("基于本体建模与大模型的语义规则编译器接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LOGOS Team")
                                .email("logos@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("/api/logos/v1").description("本地开发环境")
                ));
    }
}
