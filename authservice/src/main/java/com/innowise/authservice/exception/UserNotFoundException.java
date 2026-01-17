package com.innowise.authservice.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String field, String value) {
    super("User not found with " + field + ": " + value);
  }
}
