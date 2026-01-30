package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  Optional<User> findByEmail(String email);

  @Modifying
  @Query("UPDATE User u SET u.active = :status WHERE u.id = :id")
  void updateUserStatus(@Param("id") Long id, @Param("status") boolean status);
}
