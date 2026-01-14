package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API error response")
public class ErrorResponseDto {

  @Schema(description = "Error timestamp", example = "2026-01-15T10:30:00")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code", example = "400")
  private Integer status;

  @Schema(description = "Error name", example = "Bad Request")
  private String error;

  @Schema(
      description = "Detailed error description",
      example = "Validation failed for argument [0] in public ResponseEntity")
  private String message;

  @Schema(description = "Path to API endpoint where error occurred", example = "/api/v1/users")
  private String path;

  @Schema(
      description = "List of detailed validation errors",
      example = "[\"Email must be valid\", \"Name cannot be empty\"]")
  private List<String> validationErrors;

  @Schema(description = "Trace ID for error tracking", example = "abc123-def456-ghi789")
  private String traceId;
}
