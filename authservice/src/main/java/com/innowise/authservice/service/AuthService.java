package com.innowise.authservice.service;

import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.request.SignInRequest;
import com.innowise.authservice.model.dto.request.UserRequest;
import com.innowise.authservice.model.dto.request.ValidationTokenRequest;
import com.innowise.authservice.model.dto.response.TokenResponse;
import com.innowise.authservice.model.dto.response.ValidationTokenResponse;

/**
 * Service responsible for authentication and authorization operations.
 *
 * <p>Provides functionality for:
 *
 * <ul>
 *   <li>User registration
 *   <li>User authentication (login)
 *   <li>JWT access token refresh
 *   <li>JWT token validation
 * </ul>
 */
public interface AuthService {

  /**
   * Registers a new user in the system.
   *
   * @param request user registration data
   * @throws com.innowise.authservice.exception.UserAlreadyExistsException if a user with the given
   *     email already exists
   */
  void register(UserRequest request);

  /**
   * Authenticates a user and generates JWT access and refresh tokens.
   *
   * @param request user login credentials
   * @return {@link TokenResponse} containing access and refresh tokens
   * @throws com.innowise.authservice.exception.UserNotFoundException if user with given email does
   *     not exist
   * @throws com.innowise.authservice.exception.AuthenticationFailedException if credentials are
   *     invalid
   */
  TokenResponse login(SignInRequest request);

  /**
   * Generates a new access token using a valid refresh token.
   *
   * @param request refresh token request
   * @return {@link TokenResponse} containing new access and refresh tokens
   * @throws com.innowise.authservice.exception.InvalidRefreshTokenException if refresh token is
   *     invalid or expired
   */
  TokenResponse refreshToken(RefreshTokenRequest request);

  /**
   * Validates a JWT access token and extracts user details from it.
   *
   * @param request token validation request
   * @return {@link ValidationTokenResponse} with token validity and user data
   * @throws com.innowise.authservice.exception.TokenValidationException if token is invalid or
   *     cannot be processed
   */
  ValidationTokenResponse validateToken(ValidationTokenRequest request);
}
