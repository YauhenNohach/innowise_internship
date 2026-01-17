package com.innowise.authservice.service;

import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.request.SignInRequest;
import com.innowise.authservice.model.dto.request.UserRequest;
import com.innowise.authservice.model.dto.request.ValidationTokenRequest;
import com.innowise.authservice.model.dto.response.TokenResponse;
import com.innowise.authservice.model.dto.response.ValidationTokenResponse;

public interface AuthService {
  void register(UserRequest request);

  TokenResponse login(SignInRequest request);

  TokenResponse refreshToken(RefreshTokenRequest request);

  ValidationTokenResponse validateToken(ValidationTokenRequest request);
}
