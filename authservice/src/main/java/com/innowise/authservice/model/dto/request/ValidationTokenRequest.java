package com.innowise.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for token validation request")
public class ValidationTokenRequest {

  @Schema(
      description = "JWT access token",
      example =
          "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwucnUiLCJleHAiOjE3MDAwMDAwMDB9.signature",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Token is required")
  @Size(min = 10, max = 2000, message = "Token length is invalid")
  private String token;
}
