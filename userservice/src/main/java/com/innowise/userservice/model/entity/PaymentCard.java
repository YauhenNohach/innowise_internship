package com.innowise.userservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "payment_cards",
    indexes = {
      @Index(name = "idx_payment_cards_user_id", columnList = "user_id"),
      @Index(name = "idx_payment_cards_number", columnList = "number"),
      @Index(name = "idx_payment_cards_holder", columnList = "holder"),
      @Index(name = "idx_payment_cards_active", columnList = "active"),
    })
@Getter
@Setter
@NoArgsConstructor
public class PaymentCard extends BaseAuditEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(unique = true, nullable = false, length = 20)
  private String number;

  @Column(nullable = false, length = 50)
  private String holder;

  @Column(nullable = false, length = 5)
  private String expirationDate;

  @Column(nullable = false)
  private boolean active = true;
}
