package com.innowise.authservice.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.BaseIntegrationTest;
import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.request.SignInRequest;
import com.innowise.authservice.model.dto.request.UserRequest;
import com.innowise.authservice.model.dto.request.ValidationTokenRequest;
import com.innowise.authservice.model.dto.response.TokenResponse;
import com.innowise.authservice.repository.UserRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private ObjectMapper objectMapper;

  private TokenResponse testTokens;
  private final String testUserEmail = "integration@mail.ru";

  @BeforeEach
  void setUp() throws Exception {
    cleanUp();
    registerTestUser();
    testTokens = loginAndGetTokens();
  }

  @AfterEach
  void cleanUp() {
    userRepository.deleteAll();
  }

  private void registerTestUser() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("IntegrationUser");
    request.setEmail(testUserEmail);
    request.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  private TokenResponse loginAndGetTokens() throws Exception {
    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail(testUserEmail);
    loginRequest.setPassword("password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    return objectMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class);
  }

  @Test
  void shouldRegisterUserSuccessfully() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("NewUser");
    request.setEmail("newuser@mail.ru");
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
  void shouldReturn400WhenRegisterWithInvalidEmail() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("InvalidUser");
    request.setEmail("invalid-email");
    request.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenRegisterWithEmptyFields() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("");
    request.setEmail("");
    request.setPassword("");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldLoginSuccessfullyAndReturnTokens() throws Exception {
    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail(testUserEmail);
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
    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail(testUserEmail);
    loginRequest.setPassword("wrongpassword");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturn401WhenLoginWithNonExistentEmail() throws Exception {
    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail("nonexistent@mail.ru");
    loginRequest.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldRefreshTokenSuccessfully() throws Exception {
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
    refreshRequest.setRefreshToken(testTokens.getRefreshToken());

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken", notNullValue()))
        .andExpect(jsonPath("$.refreshToken", notNullValue()))
        .andExpect(jsonPath("$.type").value("Bearer"));
  }

  @Test
  void shouldReturn401WhenRefreshWithInvalidToken() throws Exception {
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
    refreshRequest.setRefreshToken("invalid.refresh.token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void shouldReturn400WhenRefreshWithEmptyToken() throws Exception {
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
    refreshRequest.setRefreshToken("");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldValidateTokenSuccessfully() throws Exception {
    ValidationTokenRequest validateRequest = new ValidationTokenRequest();
    validateRequest.setToken(testTokens.getAccessToken());

    mockMvc
        .perform(
            post("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid").value(true))
        .andExpect(jsonPath("$.email").value(testUserEmail))
        .andExpect(jsonPath("$.role").value("ROLE_USER"))
        .andExpect(jsonPath("$.expiresAt", notNullValue()));
  }

  @Test
  void shouldReturn400WhenValidateWithEmptyToken() throws Exception {
    ValidationTokenRequest validateRequest = new ValidationTokenRequest();
    validateRequest.setToken("");

    mockMvc
        .perform(
            post("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenRegisterExistingUser() throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername("DuplicateUser");
    request.setEmail(testUserEmail);
    request.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @ParameterizedTest
  @MethodSource("invalidUserRequests")
  void shouldReturn400WhenRegisterWithInvalidData(String username, String email, String password)
      throws Exception {
    UserRequest request = new UserRequest();
    request.setUsername(username);
    request.setEmail(email);
    request.setPassword(password);

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnTokensWithCorrectType() throws Exception {
    SignInRequest loginRequest = new SignInRequest();
    loginRequest.setEmail(testUserEmail);
    loginRequest.setPassword("password123");

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    TokenResponse response =
        objectMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class);

    assert response.getAccessToken() != null;
    assert response.getRefreshToken() != null;
    assert "Bearer".equals(response.getType());
  }

  private static Stream<Arguments> invalidUserRequests() {
    return Stream.of(
        Arguments.of(null, "test@mail.ru", "password123"),
        Arguments.of("User", null, "password123"),
        Arguments.of("User", "test@mail.ru", null),
        Arguments.of("U", "test@mail.ru", "password123"),
        Arguments.of("User", "invalid-email", "password123"),
        Arguments.of("User", "test@mail.ru", "123"));
  }
}
