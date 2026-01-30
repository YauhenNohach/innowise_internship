package com.innowise.authservice.model.dto.request;

import com.innowise.authservice.model.entity.type.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for user registration request")
public class UserRequest {

  @Schema(
      description = "Username",
      example = "yNohach",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @Schema(
      description = "User password",
      example = "1234w0rd1234",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  private String password;

  @Schema(
      description = "User email address",
      example = "test@mail.ru",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Schema(description = "User role", example = "USER", accessMode = Schema.AccessMode.READ_ONLY)
  private RoleType role = RoleType.USER;
}
