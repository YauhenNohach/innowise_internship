package com.innowise.userservice.service;

import com.innowise.userservice.model.entity.PaymentCard;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing {@link PaymentCard} entities.
 *
 * <p>Defines business operations for creating, retrieving, updating, activating, deactivating and
 * deleting payment cards.
 */
public interface CardService {

  /**
   * Creates a new payment card for a specific user.
   *
   * <p>Business rules:
   *
   * <ul>
   *   <li>User must exist
   *   <li>Card must not be expired
   *   <li>User must not exceed the maximum number of active cards
   * </ul>
   *
   * @param card payment card data
   * @param userId identifier of the card owner
   * @return created {@link PaymentCard}
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user does not exist
   * @throws com.innowise.userservice.exception.CardExpiredException if the card expiration date is
   *     in the past
   * @throws com.innowise.userservice.exception.MaxCardsLimitException if the user already has the
   *     maximum allowed number of active cards
   */
  PaymentCard createCard(PaymentCard card, Long userId);

  /**
   * Retrieves a payment card by its identifier.
   *
   * @param id payment card identifier
   * @return found {@link PaymentCard}
   * @throws com.innowise.userservice.exception.CardNotFoundException if the card is not found
   */
  PaymentCard getCardById(Long id);

  /**
   * Retrieves a paginated list of payment cards with optional filtering.
   *
   * @param holder card holder name (optional)
   * @param number card number or part of it (optional)
   * @param active card active status (optional)
   * @param pageable pagination and sorting information
   * @return page of {@link PaymentCard} matching the filters
   */
  Page<PaymentCard> getAllCards(String holder, String number, Boolean active, Pageable pageable);

  /**
   * Retrieves all payment cards belonging to a specific user.
   *
   * @param userId user identifier
   * @return list of {@link PaymentCard} owned by the user
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user does not exist
   */
  List<PaymentCard> getCardsByUserId(Long userId);

  /**
   * Retrieves a paginated list of payment cards belonging to a specific user.
   *
   * @param userId user identifier
   * @param pageable pagination and sorting information
   * @return page of {@link PaymentCard} owned by the user
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user does not exist
   */
  Page<PaymentCard> getCardsByUserId(Long userId, Pageable pageable);

  /**
   * Updates an existing payment card.
   *
   * @param id identifier of the card to update
   * @param updatedCard new card data
   * @return updated {@link PaymentCard}
   * @throws com.innowise.userservice.exception.CardNotFoundException if the card is not found
   */
  PaymentCard updateCard(Long id, PaymentCard updatedCard);

  /**
   * Updates card status (active/inactive).
   *
   * @param id identifier of the card to update
   * @param active new status value (true for active, false for inactive)
   * @return updated {@link PaymentCard}
   * @throws com.innowise.userservice.exception.CardNotFoundException if the card is not found
   */
  PaymentCard updateCardStatus(Long id, Boolean active);

  /**
   * Activates a payment card.
   *
   * @param id payment card identifier
   * @throws com.innowise.userservice.exception.CardNotFoundException if the card is not found
   */
  void activateCard(Long id);

  /**
   * Deactivates a payment card.
   *
   * @param id payment card identifier
   * @throws com.innowise.userservice.exception.CardNotFoundException if the card is not found
   */
  void deactivateCard(Long id);

  /**
   * Deletes a payment card by its identifier.
   *
   * @param id payment card identifier
   * @throws com.innowise.userservice.exception.CardNotFoundException if the card is not found
   */
  void deleteCard(Long id);
}
