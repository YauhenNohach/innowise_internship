package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing {@link User} entities.
 *
 * <p>Defines business operations for creating, retrieving, updating, activating, deactivating and
 * deleting users.
 */
public interface UserService {

  /**
   * Creates a new user.
   *
   * @param user user data
   * @return created {@link User}
   * @throws com.innowise.userservice.exception.UserAlreadyExistsException if a user with the same
   *     email already exists
   */
  User createUser(User user);

  /**
   * Retrieves a user by its identifier.
   *
   * @param id user identifier
   * @return found {@link User}
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user is not found
   */
  User getUserById(Long id);

  /**
   * Retrieves a user by email address.
   *
   * @param email user email
   * @return found {@link User}
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user is not found
   */
  User getUserByEmail(String email);

  /**
   * Retrieves a user together with all associated payment cards.
   *
   * @param id user identifier
   * @return {@link UserWithCardsDto} containing user data and cards
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user is not found
   */
  UserWithCardsDto getUserWithCardsById(Long id);

  /**
   * Retrieves a paginated list of users with optional filtering.
   *
   * @param name user first name (optional)
   * @param surname user last name (optional)
   * @param pageable pagination and sorting information
   * @return page of {@link User}
   */
  Page<User> getAllUsers(String name, String surname, Pageable pageable);

  /**
   * Updates an existing user.
   *
   * @param id identifier of the user to update
   * @param updatedUser new user data
   * @return updated {@link User}
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user is not found
   */
  User updateUser(Long id, User updatedUser);

  /**
   * Updates user status (active/inactive).
   *
   * @param id identifier of the user to update
   * @param active new status value (true for active, false for inactive)
   * @return updated {@link User}
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user is not found
   */
  User updateUserStatus(Long id, Boolean active);

  /**
   * Activates a user.
   *
   * @param id user identifier
   */
  void activateUser(Long id);

  /**
   * Deactivates a user.
   *
   * @param id user identifier
   */
  void deactivateUser(Long id);

  /**
   * Deletes a user by its identifier.
   *
   * @param id user identifier
   * @throws com.innowise.userservice.exception.UserNotFoundException if the user is not found
   */
  void deleteUser(Long id);
}
