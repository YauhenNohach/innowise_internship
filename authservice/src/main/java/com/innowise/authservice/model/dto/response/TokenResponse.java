package com.innowise.authservice.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
  private String accessToken;
  private String refreshToken;
  private String type = "Bearer";

  public TokenResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
