package com.innowise.userservice.repository.specification;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder {

  private SpecificationBuilder() {
    throw new IllegalStateException("Utility class");
  }

  public static <T> Specification<T> likeIgnoreCase(String field, String value) {
    return (root, query, cb) -> {
      if (value == null || value.trim().isEmpty()) {
        return cb.conjunction();
      }
      Path<String> fieldPath = root.get(field);
      return cb.like(cb.lower(fieldPath), "%" + value.toLowerCase().trim() + "%");
    };
  }

  public static <T> Specification<T> hasField(String field, Object value) {
    return (root, query, cb) -> {
      if (value == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get(field), value);
    };
  }

  public static <T> Specification<T> like(String field, String value) {
    return (root, query, cb) -> {
      if (value == null || value.trim().isEmpty()) {
        return cb.conjunction();
      }
      return cb.like(root.get(field), "%" + value.trim() + "%");
    };
  }

  public static <T> Specification<T> joinEqual(String joinField, String field, Object value) {
    return (root, query, cb) -> {
      if (value == null) {
        return cb.conjunction();
      }
      return cb.equal(root.join(joinField).get(field), value);
    };
  }
}
