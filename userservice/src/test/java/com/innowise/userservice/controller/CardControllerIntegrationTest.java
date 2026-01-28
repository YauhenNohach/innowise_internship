package com.innowise.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.BaseIntegrationTest;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.dto.StatusUpdateDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.AuthorizationService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private PaymentCardRepository cardRepository;

  @Autowired private ObjectMapper objectMapper;

  @MockBean(name = "authorizationService")
  private AuthorizationService authorizationService;

  private User user;
  private PaymentCard card;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setName("test");
    user.setSurname("test");
    user.setBirthDate(LocalDate.of(2026, 1, 1));
    user.setEmail("test@mail.ru");
    user = userRepository.save(user);

    card = new PaymentCard();
    card.setUser(user);
    card.setNumber("1111222233334444");
    card.setHolder("test test");
    card.setExpirationDate("1/26");
    card = cardRepository.save(card);

    when(authorizationService.hasAdminRole(any())).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    cardRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void createCard_whenValidCard_shouldReturnCreated() throws Exception {
    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("5555666677778888");
    cardDto.setHolder("test test");
    cardDto.setExpirationDate("01/26");
    cardDto.setActive(true);

    mockMvc
        .perform(
            post("/api/v1/users/{userId}/cards", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardDto)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.number").value("5555666677778888"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void createCard_whenUserAlreadyHas5Cards_shouldReturnBadRequest() throws Exception {

    for (int i = 2; i <= 5; i++) {
      PaymentCard additionalCard = new PaymentCard();
      additionalCard.setUser(user);
      additionalCard.setNumber("116122223333444" + i);
      additionalCard.setHolder("holder" + i);
      additionalCard.setExpirationDate("06/26");
      additionalCard.setActive(true);
      cardRepository.save(additionalCard);
    }

    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("9999888877776666");
    cardDto.setHolder("test test");
    cardDto.setExpirationDate("01/26");
    cardDto.setActive(true);

    mockMvc
        .perform(
            post("/api/v1/users/{userId}/cards", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getCardById_whenCardExists_shouldReturnCard() throws Exception {

    mockMvc
        .perform(get("/api/v1/cards/{id}", card.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value("1111222233334444"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getCardsByUserId_shouldReturnListOfCards() throws Exception {

    mockMvc
        .perform(get("/api/v1/users/{userId}/cards", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateCard_whenValidUpdate_shouldReturnOk() throws Exception {
    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setHolder("test test");
    cardDto.setNumber("1111222233334444");
    cardDto.setExpirationDate("01/26");
    cardDto.setActive(true);

    mockMvc
        .perform(
            put("/api/v1/cards/{id}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.holder").value("test test"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deleteCard_shouldReturnNoContent() throws Exception {

    mockMvc.perform(delete("/api/v1/cards/{id}", card.getId())).andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateCardStatus_activateCard_shouldReturnOk() throws Exception {
    StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
    statusUpdateDto.setActive(true);

    mockMvc
        .perform(
            patch("/api/v1/cards/{id}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void updateCardStatus_deactivateCard_shouldReturnOk() throws Exception {
    StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
    statusUpdateDto.setActive(false);

    mockMvc
        .perform(
            patch("/api/v1/cards/{id}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getAllCardsWithFilters_shouldReturnFilteredCards() throws Exception {

    mockMvc
        .perform(get("/api/v1/cards").param("holder", "test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].holder").value("test test"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getAllCardsWithFilters_byNumber_shouldReturnFilteredCards() throws Exception {

    mockMvc
        .perform(get("/api/v1/cards").param("number", "4444"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getAllCardsWithFilters_byActiveStatus_shouldReturnFilteredCards() throws Exception {

    mockMvc
        .perform(get("/api/v1/cards").param("active", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].active").value(true));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getAllCardsWithPagination_shouldReturnPagedResults() throws Exception {
    for (int i = 2; i <= 3; i++) {
      User additionalUser = new User();
      additionalUser.setName("user" + i);
      additionalUser.setSurname("surname" + i);
      additionalUser.setBirthDate(LocalDate.of(1990, 1, i));
      additionalUser.setEmail("user" + i + "@mail.ru");
      additionalUser = userRepository.save(additionalUser);

      PaymentCard additionalCard = new PaymentCard();
      additionalCard.setUser(additionalUser);
      additionalCard.setNumber("111122223333444" + i);
      additionalCard.setHolder("holder" + i);
      additionalCard.setExpirationDate("01/26");
      cardRepository.save(additionalCard);
    }

    mockMvc
        .perform(get("/api/v1/cards").param("page", "0").param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.numberOfElements").value(2));
  }
}
