package com.innowise.userservice.constant;

public final class ApiConstant {
  private ApiConstant() {}

  public static final String API_V1 = "/api/v1";
  public static final String USERS = "/users";
  public static final String CARDS = "/cards";
  public static final String USERS_BASE = API_V1 + USERS;
  public static final String CARDS_BASE = API_V1 + CARDS;

  public static final String USER_ID_PATH = "/{id}";
  public static final String ACTIVATE_USER = "/{id}/activate";
  public static final String DEACTIVATE_USER = "/{id}/deactivate";

  public static final String USER_CARDS_OPERATIONS = "/{userId}/cards";
}
