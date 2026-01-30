package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User DTO with full information including linked payment cards")
public class UserWithCardsDto implements Serializable {

  @Schema(
      description = "Unique user identifier",
      example = "1",
      accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "User's first name", example = "Yuahen")
  private String name;

  @Schema(description = "User's last name", example = "Nohach")
  private String surname;

  @Schema(description = "User's birth date", example = "2000-01-15")
  private LocalDate birthDate;

  @Schema(description = "User's email address", example = "test@mail.ru")
  private String email;

  @Schema(description = "User account active status", example = "true")
  private Boolean active;

  @Schema(description = "Record creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Record last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  @Schema(description = "List of user's payment cards")
  private List<PaymentCardDto> cards;
}
