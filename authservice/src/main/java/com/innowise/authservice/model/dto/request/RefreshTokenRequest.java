package com.innowise.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for refresh token request")
public class RefreshTokenRequest {

  @Schema(
      description = "JWT refresh token",
      example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZdfgeHAiOjE3MDAwMDAwMDB9.sdhdfsg",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Refresh token is required")
  @Size(min = 10, max = 2000, message = "Refresh token length is invalid")
  private String refreshToken;
}
