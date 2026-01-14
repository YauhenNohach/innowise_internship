package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.PaymentCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentCardRepository
    extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

  @Query("SELECT c FROM PaymentCard c WHERE c.user.id = :userId")
  List<PaymentCard> findAllByUserId(@Param("userId") Long userId);

  @Query(
      value = "SELECT COUNT(*) FROM payment_cards WHERE user_id = :userId AND active = true",
      nativeQuery = true)
  int countActiveCardsByUserId(@Param("userId") Long userId);

  @Modifying
  @Query("UPDATE PaymentCard c SET c.active = :status WHERE c.id = :id")
  void updateCardStatus(@Param("id") Long id, @Param("status") boolean status);
}
