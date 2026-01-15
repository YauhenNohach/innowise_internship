package com.innowise.userservice.repository.specification;

import static com.innowise.userservice.repository.specification.SpecificationBuilder.likeIgnoreCase;

import com.innowise.userservice.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

  private UserSpecification() {
    throw new IllegalStateException("Utility class");
  }

  public static Specification<User> filterByNameAndSurname(String name, String surname) {
    return Specification.<User>where(null)
        .and(likeIgnoreCase("name", name))
        .and(likeIgnoreCase("surname", surname));
  }
}
