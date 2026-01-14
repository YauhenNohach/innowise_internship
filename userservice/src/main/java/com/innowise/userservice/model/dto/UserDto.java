package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for transferring user data")
public class UserDto {

  @Schema(
      description = "Unique user identifier",
      example = "1",
      accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(
      description = "User's first name",
      example = "Yauhen",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  @Schema(
      description = "User's last name",
      example = "Nohach",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Surname is required")
  @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
  private String surname;

  @Schema(
      description = "User's birth date",
      example = "2000-01-15",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Birth date is required")
  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;

  @Schema(
      description = "User's email address",
      example = "test@mail.ru",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Schema(description = "User account active status", example = "true")
  private Boolean active;

  @Schema(description = "Record creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Record last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;
}
