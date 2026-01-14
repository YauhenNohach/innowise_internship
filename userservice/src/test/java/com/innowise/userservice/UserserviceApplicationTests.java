package com.innowise.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@TestConfiguration
@Profile("test")
class UserserviceApplicationTests {

  @Test
  void contextLoads() {}
}
