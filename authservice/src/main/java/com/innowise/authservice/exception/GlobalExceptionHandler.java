package com.innowise.authservice.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    log.error("User not found: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(), "User Not Found", ex.getMessage(), LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    log.error("User already exists: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "User Already Exists",
            ex.getMessage(),
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
    log.error("Invalid refresh token: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid Refresh Token",
            ex.getMessage(),
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(TokenValidationException.class)
  public ResponseEntity<ErrorResponse> handleTokenValidation(TokenValidationException ex) {
    log.error("Token validation failed: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Token Validation Failed",
            ex.getMessage(),
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationFailed(
      AuthenticationFailedException ex) {
    log.error("Authentication failed: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication Failed",
            ex.getMessage(),
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    log.error("Bad credentials: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication Failed",
            "Invalid email or password",
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    log.error("Access denied: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            "You don't have permission to access this resource",
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
    log.error("JWT expired: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Token Expired",
            "JWT token has expired",
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex) {
    log.error("JWT signature invalid: {}", ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid Token",
            "JWT signature is invalid",
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error("Validation failed: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ValidationErrorResponse errorResponse =
        new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Input validation failed",
            LocalDateTime.now(),
            errors);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
