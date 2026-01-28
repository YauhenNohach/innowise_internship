package com.innowise.userservice.controller;

import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.dto.StatusUpdateDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("@authorizationService.hasAdminRole(authentication)")
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
  @PreAuthorize("@authorizationService.hasAdminRole(authentication)")
  @GetMapping(ApiConstant.CARD_ID_PATH)
  public ResponseEntity<PaymentCardDto> getCardById(
      @Parameter(description = "ID of the card to retrieve", required = true) @PathVariable("id")
          Long id) {

    PaymentCard card = cardService.getCardById(id);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(card);
    return ResponseEntity.ok(responseDto);
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
  @PutMapping(ApiConstant.CARD_ID_PATH)
  @PreAuthorize("@authorizationService.hasAdminRole(authentication)")
  public ResponseEntity<PaymentCardDto> updateCard(
      @Parameter(description = "ID of the card to update", required = true) @PathVariable("id")
          Long id,
      @Parameter(description = "Updated card data", required = true) @Valid @RequestBody
          PaymentCardDto cardDto) {

    PaymentCard card = cardMapper.cardDtoToCard(cardDto);
    PaymentCard updatedCard = cardService.updateCard(id, card);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(updatedCard);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(summary = "Delete card", description = "Deletes payment card by ID")
  @ApiResponse(responseCode = "204", description = "Card deleted successfully")
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @DeleteMapping(ApiConstant.CARD_ID_PATH)
  @PreAuthorize("@authorizationService.hasAdminRole(authentication)")
  public ResponseEntity<Void> deleteCard(
      @Parameter(description = "ID of the card to delete", required = true) @PathVariable("id")
          Long id) {

    cardService.deleteCard(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Update card status", description = "Updates card active status by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Card status updated successfully",
      content = @Content(schema = @Schema(implementation = PaymentCardDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid status data",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Card not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PreAuthorize("@authorizationService.hasAdminRole(authentication)")
  @PatchMapping(ApiConstant.CARD_ID_PATH)
  public ResponseEntity<PaymentCardDto> updateCardStatus(
      @Parameter(description = "ID of the card to update", required = true) @PathVariable("id")
          Long id,
      @Parameter(description = "Status update data", required = true) @Valid @RequestBody
          StatusUpdateDto statusUpdateDto) {

    PaymentCard updatedCard = cardService.updateCardStatus(id, statusUpdateDto.getActive());
    PaymentCardDto responseDto = cardMapper.cardToCardDto(updatedCard);
    return ResponseEntity.ok(responseDto);
  }
}
