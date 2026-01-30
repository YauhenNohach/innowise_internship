package com.innowise.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI userServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Auth Service API")
                .description("REST API for authentication")
                .version("1.0.0"))
        .servers(
            List.of(new Server().url("http://localhost:8082").description("Development server")));
  }
}
