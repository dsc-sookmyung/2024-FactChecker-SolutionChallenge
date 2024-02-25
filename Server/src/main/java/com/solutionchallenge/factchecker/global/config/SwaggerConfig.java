package com.solutionchallenge.factchecker.global.config;

import org.springdoc.core.GroupedOpenApi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1-definition")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Fact-Checker")
                        .description("GDSC 2024 Team Fact-Checker <TrueTree> 서비스입니다.")
                        .version("v0.0.1"));
    }
}
