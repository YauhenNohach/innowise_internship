package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  User createUser(User user);

  User getUserById(Long id);

  User getUserByEmail(String email);

  UserWithCardsDto getUserWithCardsById(Long id);

  Page<User> getAllUsers(String name, String surname, Pageable pageable);

  User updateUser(Long id, User updatedUser);

  void activateUser(Long id);

  void deactivateUser(Long id);

  void deleteUser(Long id);
}
