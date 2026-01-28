package com.innowise.userservice.exception;

public class CardExpiredException extends RuntimeException {

  public CardExpiredException(String expirationDate) {
    super("Card is expired. Expiration date: " + expirationDate);
  }
}
