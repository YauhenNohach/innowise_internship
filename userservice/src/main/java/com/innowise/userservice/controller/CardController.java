package com.innowise.userservice.controller;

import com.innowise.userservice.constant.ApiConstant;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.CARDS_BASE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Card Management", description = "API for managing payment cards")
public class CardController {

  private final CardService cardService;
  private final PaymentCardMapper cardMapper;

  @Operation(summary = "Get all by filters", description = "Returns payment card information")
  @ApiResponse(
      responseCode = "200",
      description = "Cards found",
      content =
          @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = PaymentCardDto.class))))
  @ApiResponse(
      responseCode = "404",
      description = "Cards not found",
      content =
          @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
  @GetMapping
  public ResponseEntity<Page<PaymentCardDto>> getAllCardsWithFilters(
      @RequestParam(required = false) String holder,
      @RequestParam(required = false) String number,
      @RequestParam(required = false) Boolean active,
      Pageable pageable) {

    Page<PaymentCard> cards = cardService.getAllCards(holder, number, active, pageable);
    Page<PaymentCardDto> responseDtos = cards.map(cardMapper::cardToCardDto);
    return ResponseEntity.ok(responseDtos);
  }

  @Operation(summary = "Get card by ID", description = "Returns payment card information by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Card found",
      content = @Content(schema = @Schema(implementation = PaymentCardDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardDto> getCardById(
      @Parameter(description = "ID of the card to retrieve", required = true) @PathVariable("id")
          Long id) {

    log.info("Fetching card with ID: {}", id);
    PaymentCard card = cardService.getCardById(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(card);
    log.info("Card fetched successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(summary = "Get all cards", description = "Returns all payment cards in the system")
  @ApiResponse(
      responseCode = "200",
      description = "All cards retrieved successfully",
      content =
          @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentCardDto.class))))
  @GetMapping("/all")
  public ResponseEntity<List<PaymentCardDto>> getAllCards() {

    log.info("Fetching all cards");
    List<PaymentCard> cards = cardService.getAllCards();
    List<PaymentCardDto> responseDtos = cards.stream().map(cardMapper::cardToCardDto).toList();
    log.info("Found {} total cards", responseDtos.size());
    return ResponseEntity.ok(responseDtos);
  }

  @Operation(summary = "Update card", description = "Updates payment card information by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Card updated successfully",
      content = @Content(schema = @Schema(implementation = PaymentCardDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid card data",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PutMapping("/{id}")
  public ResponseEntity<PaymentCardDto> updateCard(
      @Parameter(description = "ID of the card to update", required = true) @PathVariable("id")
          Long id,
      @Parameter(description = "Updated card data", required = true) @Valid @RequestBody
          PaymentCardDto cardDto) {

    log.info("Updating card with ID: {}", id);
    PaymentCard card = cardMapper.cardDtoToCard(cardDto);
    PaymentCard updatedCard = cardService.updateCard(id, card);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(updatedCard);
    log.info("Card updated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(summary = "Delete card", description = "Deletes payment card by ID")
  @ApiResponse(responseCode = "204", description = "Card deleted successfully")
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCard(
      @Parameter(description = "ID of the card to delete", required = true) @PathVariable("id")
          Long id) {

    log.info("Deleting card with ID: {}", id);
    cardService.deleteCard(id);
    log.info("Card deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Activate card", description = "Activates a payment card by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "Card activated successfully",
      content = @Content(schema = @Schema(implementation = PaymentCardDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PatchMapping("/{id}/activate")
  public ResponseEntity<PaymentCardDto> activateCard(
      @Parameter(description = "ID of the card to activate", required = true) @PathVariable("id")
          Long id) {

    log.info("Activating card with ID: {}", id);
    PaymentCard activatedCard = cardService.activateCard(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(activatedCard);
    log.info("Card activated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(summary = "Deactivate card", description = "Deactivates a payment card by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "Card deactivated successfully",
      content = @Content(schema = @Schema(implementation = PaymentCardDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<PaymentCardDto> deactivateCard(
      @Parameter(description = "ID of the card to deactivate", required = true) @PathVariable("id")
          Long id) {

    log.info("Deactivating card with ID: {}", id);
    PaymentCard deactivatedCard = cardService.deactivateCard(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(deactivatedCard);
    log.info("Card deactivated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }
}
