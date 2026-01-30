package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.CardExpiredException;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.MaxCardsLimitException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.CardSpecification;
import com.innowise.userservice.service.CardService;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardServiceImpl implements CardService {

  private final PaymentCardRepository cardRepository;
  private final UserRepository userRepository;
  private static final Integer MAX_CARDS_PER_USER = 5;

  @Override
  public PaymentCard createCard(PaymentCard card, Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    YearMonth expiry =
        YearMonth.parse(card.getExpirationDate(), DateTimeFormatter.ofPattern("MM/yy"));
    if (expiry.isBefore(YearMonth.now())) {
      throw new CardExpiredException(card.getExpirationDate());
    }
    int activeCardsCount = cardRepository.countActiveCardsByUserId(userId);
    if (activeCardsCount >= MAX_CARDS_PER_USER) {
      log.warn("User {} already has {} active cards, limit exceeded", userId, activeCardsCount);
      throw new MaxCardsLimitException();
    }

    card.setUser(user);
    return cardRepository.save(card);
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard getCardById(Long id) {
    return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaymentCard> getAllCards(
      String holder, String number, Boolean active, Pageable pageable) {
    Specification<PaymentCard> spec =
        Specification.where(CardSpecification.filterByHolderAndNumber(holder, number))
            .and(CardSpecification.filterByActive(active));

    return cardRepository.findAll(spec, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentCard> getCardsByUserId(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }
    return cardRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaymentCard> getCardsByUserId(Long userId, Pageable pageable) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }

    Specification<PaymentCard> spec = CardSpecification.filterByUserId(userId);
    return cardRepository.findAll(spec, pageable);
  }

  @Override
  @CacheEvict(value = "userWithCards", key = "#result.user.id")
  public PaymentCard updateCard(Long id, PaymentCard updatedCard) {
    PaymentCard existingCard = getCardById(id);

    if (updatedCard.getNumber() != null) {
      existingCard.setNumber(updatedCard.getNumber());
    }
    if (updatedCard.getHolder() != null) {
      existingCard.setHolder(updatedCard.getHolder());
    }
    if (updatedCard.getExpirationDate() != null) {
      existingCard.setExpirationDate(updatedCard.getExpirationDate());
    }

    return cardRepository.save(existingCard);
  }

  @Override
  @CacheEvict(value = "userWithCards", key = "#result.user.id")
  public PaymentCard updateCardStatus(Long id, Boolean active) {
    PaymentCard card = getCardById(id);
    card.setActive(active);
    return cardRepository.save(card);
  }

  @Override
  @CacheEvict(value = "userWithCards", key = "#result.user.id")
  public void activateCard(Long id) {
    updateCardStatus(id, true);
  }

  @Override
  @CacheEvict(value = "userWithCards", key = "#result.user.id")
  public void deactivateCard(Long id) {
    updateCardStatus(id, false);
  }

  @Override
  @CacheEvict(value = "userWithCards", key = "#id")
  public void deleteCard(Long id) {
    PaymentCard card = getCardById(id);
    Long userId = card.getUser().getId();

    log.debug("Deleting card with id: {} for user: {}", id, userId);
    cardRepository.deleteById(id);
  }
}
