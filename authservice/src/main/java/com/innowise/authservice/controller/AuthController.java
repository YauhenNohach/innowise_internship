package com.innowise.authservice.controller;

import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.request.SignInRequest;
import com.innowise.authservice.model.dto.request.UserRequest;
import com.innowise.authservice.model.dto.request.ValidationTokenRequest;
import com.innowise.authservice.model.dto.response.TokenResponse;
import com.innowise.authservice.model.dto.response.ValidationTokenResponse;
import com.innowise.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Authentication Management",
    description = "API for user authentication, registration and token validation")
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "Register new user",
      description = "Creates a new user account in the system")
  @ApiResponse(
      responseCode = "200",
      description = "User registered successfully",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid registration data or user already exists",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PostMapping("/register")
  public ResponseEntity<String> register(@Valid @RequestBody UserRequest request) {
    log.info("Registering new user: {}", request.getUsername());
    authService.register(request);
    log.info("User {} registered successfully", request.getUsername());
    return ResponseEntity.ok("User registered successfully");
  }

  @Operation(
      summary = "User login",
      description = "Authenticates user and returns access and refresh tokens")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully authenticated",
      content = @Content(schema = @Schema(implementation = TokenResponse.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Invalid credentials",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody SignInRequest request) {
    log.info("Received login request for user: {}", request.getEmail());
    TokenResponse response = authService.login(request);
    log.info("User {} authenticated successfully", request.getEmail());
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Refresh token",
      description = "Generates new tokens using a valid refresh token")
  @ApiResponse(
      responseCode = "200",
      description = "Tokens refreshed successfully",
      content = @Content(schema = @Schema(implementation = TokenResponse.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Invalid or expired refresh token",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    log.info("Received token refresh request");
    TokenResponse response = authService.refreshToken(request);
    log.info("Tokens refreshed successfully");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Validate token", description = "Checks if the provided token is valid")
  @ApiResponse(
      responseCode = "200",
      description = "Token validation processed",
      content = @Content(schema = @Schema(implementation = ValidationTokenResponse.class)))
  @PostMapping("/validate")
  public ResponseEntity<ValidationTokenResponse> validateToken(
      @Valid @RequestBody ValidationTokenRequest request) {
    log.info("Validating token");
    ValidationTokenResponse response = authService.validateToken(request);
    log.info("Token validation completed. Status: {}", response.isValid());
    return ResponseEntity.ok(response);
  }
}
