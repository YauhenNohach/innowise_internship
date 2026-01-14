package com.innowise.userservice.controller;

import com.innowise.userservice.constant.ApiConstant;
import com.innowise.userservice.controller.api.CardControllerApi;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.service.CardService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.CARDS_BASE)
@RequiredArgsConstructor
@Slf4j
public class CardController implements CardControllerApi {

  private final CardService cardService;
  private final PaymentCardMapper cardMapper;

  @Override
  public ResponseEntity<Page<PaymentCardDto>> getAllCardsWithFilters(
      @RequestParam(required = false) String holder,
      @RequestParam(required = false) String number,
      @RequestParam(required = false) Boolean active,
      Pageable pageable) {

    Page<PaymentCard> cards = cardService.getAllCards(holder, number, active, pageable);
    Page<PaymentCardDto> responseDtos = cards.map(cardMapper::cardToCardDto);
    return ResponseEntity.ok(responseDtos);
  }

  @Override
  public ResponseEntity<PaymentCardDto> getCardById(@PathVariable Long id) {
    log.info("Fetching card with ID: {}", id);
    PaymentCard card = cardService.getCardById(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(card);
    log.info("Card fetched successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Override
  public ResponseEntity<List<PaymentCardDto>> getAllCards() {
    log.info("Fetching all cards");
    List<PaymentCard> cards = cardService.getAllCards();
    List<PaymentCardDto> responseDtos = cards.stream().map(cardMapper::cardToCardDto).toList();
    log.info("Found {} total cards", responseDtos.size());
    return ResponseEntity.ok(responseDtos);
  }

  @Override
  public ResponseEntity<PaymentCardDto> updateCard(
      @PathVariable Long id, @Valid @RequestBody PaymentCardDto cardDto) {
    log.info("Updating card with ID: {}", id);
    PaymentCard card = cardMapper.cardDtoToCard(cardDto);
    PaymentCard updatedCard = cardService.updateCard(id, card);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(updatedCard);
    log.info("Card updated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Override
  public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
    log.info("Deleting card with ID: {}", id);
    cardService.deleteCard(id);
    log.info("Card deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PaymentCardDto> activateCard(@PathVariable Long id) {
    log.info("Activating card with ID: {}", id);
    PaymentCard activatedCard = cardService.activateCard(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(activatedCard);
    log.info("Card activated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Override
  public ResponseEntity<PaymentCardDto> deactivateCard(@PathVariable Long id) {
    log.info("Deactivating card with ID: {}", id);
    PaymentCard deactivatedCard = cardService.deactivateCard(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(deactivatedCard);
    log.info("Card deactivated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }
}
