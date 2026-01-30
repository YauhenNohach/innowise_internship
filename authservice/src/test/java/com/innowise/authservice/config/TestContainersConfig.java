package com.innowise.authservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

  @Bean(initMethod = "start", destroyMethod = "stop")
  public PostgreSQLContainer<?> postgreSQLContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));
  }
}
