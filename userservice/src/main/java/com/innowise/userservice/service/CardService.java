package com.innowise.userservice.service;

import com.innowise.userservice.model.entity.PaymentCard;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

  PaymentCard createCard(PaymentCard card, Long userId);

  PaymentCard getCardById(Long id);

  List<PaymentCard> getAllCards();

  Page<PaymentCard> getAllCards(String holder, String number, Boolean active, Pageable pageable);

  List<PaymentCard> getCardsByUserId(Long userId);

  Page<PaymentCard> getCardsByUserId(Long userId, Pageable pageable);

  PaymentCard updateCard(Long id, PaymentCard updatedCard);

  PaymentCard activateCard(Long id);

  PaymentCard deactivateCard(Long id);

  void deleteCard(Long id);
}
