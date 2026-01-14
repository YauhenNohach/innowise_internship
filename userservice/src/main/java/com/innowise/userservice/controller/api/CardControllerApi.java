package com.innowise.userservice.controller.api;

import com.innowise.userservice.constant.ApiConstant;
import com.innowise.userservice.model.dto.PaymentCardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

@RequestMapping(ApiConstant.CARDS_BASE)
@Tag(name = "Payment Card Management", description = "API for managing payment cards")
public interface CardControllerApi {

  @Operation(summary = "Get all by filters", description = "Returns payment card information")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cards found",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PaymentCardDto.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Cards not found",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = String.class)))
  })
  @GetMapping
  ResponseEntity<Page<PaymentCardDto>> getAllCardsWithFilters(
      @RequestParam(required = false) String holder,
      @RequestParam(required = false) String number,
      @RequestParam(required = false) Boolean active,
      Pageable pageable);

  @Operation(summary = "Get card by ID", description = "Returns payment card information by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card found",
            content = @Content(schema = @Schema(implementation = PaymentCardDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Card not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @GetMapping("/{id}")
  ResponseEntity<PaymentCardDto> getCardById(
      @Parameter(description = "ID of the card to retrieve", required = true) @PathVariable
          Long id);

  @Operation(summary = "Get all cards", description = "Returns all payment cards in the system")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "All cards retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class)))
      })
  @GetMapping("/all")
  ResponseEntity<List<PaymentCardDto>> getAllCards();

  @Operation(summary = "Update card", description = "Updates payment card information by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card updated successfully",
            content = @Content(schema = @Schema(implementation = PaymentCardDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Card not found",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid card data",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PutMapping("/{id}")
  ResponseEntity<PaymentCardDto> updateCard(
      @Parameter(description = "ID of the card to update", required = true) @PathVariable Long id,
      @Parameter(description = "Updated card data", required = true) @Valid @RequestBody
          PaymentCardDto cardDto);

  @Operation(summary = "Delete card", description = "Deletes payment card by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "Card not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteCard(
      @Parameter(description = "ID of the card to delete", required = true) @PathVariable Long id);

  @Operation(summary = "Activate card", description = "Activates a payment card by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card activated successfully",
            content = @Content(schema = @Schema(implementation = PaymentCardDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Card not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PatchMapping("/{id}/activate")
  ResponseEntity<PaymentCardDto> activateCard(@PathVariable Long id);

  @Operation(summary = "Deactivate card", description = "Deactivates a payment card by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card deactivated successfully",
            content = @Content(schema = @Schema(implementation = PaymentCardDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Card not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PatchMapping("/{id}/deactivate")
  ResponseEntity<PaymentCardDto> deactivateCard(@PathVariable Long id);
}
