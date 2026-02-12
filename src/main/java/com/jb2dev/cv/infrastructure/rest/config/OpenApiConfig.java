package com.jb2dev.cv.infrastructure.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI cvOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("jb2dev-cv-api")
            .version("1.0.0")
            .description("Curriculum Vitae REST API (static JSON-backed portfolio project)."));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("cv-api")
        .pathsToMatch("/api/v1/**")
        .pathsToExclude("/api/v1/openapi/**", "/api/v1/swagger-ui/**")
        .build();
  }
}
