package com.innowise.authservice.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.BaseIntegrationTest;
import com.innowise.authservice.model.dto.request.SignInRequest;
import com.innowise.authservice.model.dto.request.UserRequest;
import com.innowise.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void cleanUp() {
    userRepository.deleteAll();
  }

  @Test
  void shouldRegisterUserSuccessfully() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("yNohach");
    request.setEmail("test@mail.ru");
    request.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("User registered successfully"));
  }

  @Test
  void shouldLoginSuccessfullyAndReturnTokens() throws Exception {
    UserRequest regRequest = new UserRequest();
    regRequest.setUsername("loginUser");
    regRequest.setEmail("login@mail.ru");
    regRequest.setPassword("password123");

    mockMvc.perform(
        post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(regRequest)));

    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail("login@mail.ru");
    loginRequest.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken", notNullValue()))
        .andExpect(jsonPath("$.refreshToken", notNullValue()))
        .andExpect(jsonPath("$.type").value("Bearer"));
  }

  @Test
  void shouldReturn401WhenLoginWithWrongPassword() throws Exception {
    UserRequest regRequest = new UserRequest();
    regRequest.setUsername("wrongPassUser");
    regRequest.setEmail("wrong@mail.ru");
    regRequest.setPassword("correctPassword");

    mockMvc.perform(
        post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(regRequest)));

    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail("wrong@mail.ru");
    loginRequest.setPassword("incorrectPassword");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturn400WhenRegisterExistingUser() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("duplicate");
    request.setEmail("duplicate@mail.ru");
    request.setPassword("password");

    mockMvc.perform(
        post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }
}
