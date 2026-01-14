package com.innowise.userservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.MaxCardsLimitException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {

  @Mock private PaymentCardRepository cardRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private CardServiceImpl cardService;

  @Test
  void createCard_whenUserExistsAndCardLimitNotExceeded_shouldReturnCard() {
    User user = new User();
    user.setId(1L);
    PaymentCard card = new PaymentCard();

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(cardRepository.countActiveCardsByUserId(anyLong())).thenReturn(4);
    when(cardRepository.save(any(PaymentCard.class))).thenReturn(card);

    PaymentCard createdCard = cardService.createCard(card, 1L);

    assertNotNull(createdCard);
    verify(userRepository, times(1)).findById(anyLong());
    verify(cardRepository, times(1)).countActiveCardsByUserId(anyLong());
    verify(cardRepository, times(1)).save(any(PaymentCard.class));
  }

  @Test
  void createCard_whenUserDoesNotExist_shouldThrowException() {
    PaymentCard card = new PaymentCard();

    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> cardService.createCard(card, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(cardRepository, never()).countActiveCardsByUserId(anyLong());
    verify(cardRepository, never()).save(any(PaymentCard.class));
  }

  @Test
  void createCard_whenCardLimitExceeded_shouldThrowException() {
    User user = new User();
    user.setId(1L);
    PaymentCard card = new PaymentCard();

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(cardRepository.countActiveCardsByUserId(anyLong())).thenReturn(5);

    assertThrows(MaxCardsLimitException.class, () -> cardService.createCard(card, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(cardRepository, times(1)).countActiveCardsByUserId(anyLong());
    verify(cardRepository, never()).save(any(PaymentCard.class));
  }

  @Test
  void getCardById_whenCardExists_shouldReturnCard() {
    PaymentCard card = new PaymentCard();
    card.setId(1L);

    when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));

    PaymentCard foundCard = cardService.getCardById(1L);

    assertNotNull(foundCard);
    assertEquals(card.getId(), foundCard.getId());
    verify(cardRepository, times(1)).findById(anyLong());
  }

  @Test
  void getCardById_whenCardDoesNotExist_shouldThrowException() {
    when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1L));
    verify(cardRepository, times(1)).findById(anyLong());
  }

  @Test
  @SuppressWarnings("unchecked")
  void getAllCards_shouldReturnPageOfCards() {
    Page<PaymentCard> cardPage = new PageImpl<>(Collections.singletonList(new PaymentCard()));
    when(cardRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(cardPage);

    Page<PaymentCard> result = cardService.getAllCards(null, null, null, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(cardRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getCardsByUserId_whenUserExists_shouldReturnListOfCards() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(cardRepository.findAllByUserId(anyLong()))
        .thenReturn(Collections.singletonList(new PaymentCard()));

    List<PaymentCard> result = cardService.getCardsByUserId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(userRepository, times(1)).existsById(anyLong());
    verify(cardRepository, times(1)).findAllByUserId(anyLong());
  }

  @Test
  void getCardsByUserId_whenUserDoesNotExist_shouldThrowException() {
    when(userRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> cardService.getCardsByUserId(1L));
    verify(userRepository, times(1)).existsById(anyLong());
    verify(cardRepository, never()).findAllByUserId(anyLong());
  }

  @Test
  @SuppressWarnings("unchecked")
  void getCardsByUserIdWithPageable_whenUserExists_shouldReturnPageOfCards() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    Page<PaymentCard> cardPage = new PageImpl<>(Collections.singletonList(new PaymentCard()));
    when(cardRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(cardPage);

    Page<PaymentCard> result = cardService.getCardsByUserId(1L, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(userRepository, times(1)).existsById(anyLong());
    verify(cardRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void updateCard_whenCardExists_shouldReturnUpdatedCard() {
    PaymentCard existingCard = new PaymentCard();
    existingCard.setId(1L);
    PaymentCard updatedCard = new PaymentCard();
    updatedCard.setNumber("1234");

    when(cardRepository.findById(anyLong())).thenReturn(Optional.of(existingCard));
    when(cardRepository.save(any(PaymentCard.class))).thenReturn(updatedCard);

    PaymentCard result = cardService.updateCard(1L, updatedCard);

    assertNotNull(result);
    assertEquals(updatedCard.getNumber(), result.getNumber());
    verify(cardRepository, times(1)).findById(anyLong());
    verify(cardRepository, times(1)).save(any(PaymentCard.class));
  }

  @Test
  void updateCard_whenCardDoesNotExist_shouldThrowException() {
    PaymentCard updatedCard = new PaymentCard();
    when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(CardNotFoundException.class, () -> cardService.updateCard(1L, updatedCard));
    verify(cardRepository, times(1)).findById(anyLong());
    verify(cardRepository, never()).save(any(PaymentCard.class));
  }

  @Test
  void activateCard_shouldCallRepository() {
    User user = new User();
    user.setId(1L);
    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));
    doNothing().when(cardRepository).updateCardStatus(anyLong(), eq(true));

    cardService.activateCard(1L);

    verify(cardRepository, times(1)).findById(anyLong());
    verify(cardRepository, times(1)).updateCardStatus(1L, true);
  }

  @Test
  void deactivateCard_shouldCallRepository() {
    User user = new User();
    user.setId(1L);
    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));
    doNothing().when(cardRepository).updateCardStatus(anyLong(), eq(false));

    cardService.deactivateCard(1L);

    verify(cardRepository, times(1)).findById(anyLong());
    verify(cardRepository, times(1)).updateCardStatus(1L, false);
  }

  @Test
  void deleteCard_shouldCallRepository() {
    User user = new User();
    user.setId(1L);
    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));
    doNothing().when(cardRepository).deleteById(anyLong());

    cardService.deleteCard(1L);

    verify(cardRepository, times(1)).findById(anyLong());
    verify(cardRepository, times(1)).deleteById(anyLong());
  }
}
