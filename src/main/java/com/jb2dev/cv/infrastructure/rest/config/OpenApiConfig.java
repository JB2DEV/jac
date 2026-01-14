package com.jb2dev.cv.infrastructure.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI cvOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("jb2dev-cv-api")
            .version("2.2.1")
            .description("Curriculum Vitae REST API (static JSON-backed portfolio project)."));
  }
}
