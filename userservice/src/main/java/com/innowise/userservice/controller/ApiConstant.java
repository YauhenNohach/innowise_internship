package com.innowise.userservice.controller;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiConstant {

  public static final String API_V1 = "/api/v1";
  public static final String USERS = "/users";
  public static final String CARDS = "/cards";

  public static final String USERS_BASE = API_V1 + USERS;
  public static final String CARDS_BASE = API_V1 + CARDS;

  public static final String ID_PATH = "/{id}";
  public static final String USER_ID_PATH = ID_PATH;
  public static final String CARD_ID_PATH = ID_PATH;

  public static final String USER_CARDS_OPERATIONS = "/{userId}/cards";
}
