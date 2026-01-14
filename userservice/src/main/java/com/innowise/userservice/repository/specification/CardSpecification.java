package com.innowise.userservice.repository.specification;

import static com.innowise.userservice.repository.specification.SpecificationBuilder.*;

import com.innowise.userservice.model.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {

  public static Specification<PaymentCard> filterByHolderAndNumber(String holder, String number) {
    return Specification.<PaymentCard>where(null)
        .and(likeIgnoreCase("holder", holder))
        .and(like("number", number));
  }

  public static Specification<PaymentCard> filterByUserId(Long userId) {
    return joinEqual("user", "id", userId);
  }

  public static Specification<PaymentCard> filterByActive(Boolean active) {
    return hasField("active", active);
  }
}
