package com.innowise.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for user login request")
public class SignInRequest {

  @Schema(
      description = "User email address",
      example = "test@mail.ru",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Schema(
      description = "User password",
      example = "P@ssw0rd123",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  private String password;
}
