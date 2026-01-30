package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating status")
public class StatusUpdateDto {

  @Schema(
      description = "Status value",
      example = "true",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Active status is required")
  private Boolean active;
}
