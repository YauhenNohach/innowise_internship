package com.innowise.authservice.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
  private Map<String, String> validationErrors;

  public ValidationErrorResponse(
      int status,
      String error,
      String message,
      LocalDateTime timestamp,
      Map<String, String> validationErrors) {
    super(status, error, message, timestamp);
    this.validationErrors = validationErrors;
  }
}
