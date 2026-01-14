package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for transferring payment card data")
public class PaymentCardDto {

  @Schema(description = "Unique identifier of the payment card", example = "1")
  private Long id;

  @Schema(
      description = "Payment card number (16 digits)",
      example = "1234567890123456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Card number is required")
  @Size(min = 16, max = 16, message = "Card number must be 16 digits")
  private String number;

  @Schema(
      description = "Card holder name",
      example = "IVAN IVANOV",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Card holder name is required")
  private String holder;

  @Schema(
      description = "Card expiration date in MM/YY format",
      example = "01/26",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Expiration date is required")
  @Pattern(
      regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$",
      message = "Expiration date must be in MM/YY format")
  private String expirationDate;

  @Schema(description = "Indicates if the card is active", example = "true")
  private boolean active;
}
