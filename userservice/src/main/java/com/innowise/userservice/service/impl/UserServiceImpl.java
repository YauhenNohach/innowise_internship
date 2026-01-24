package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.UserAlreadyExistsException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecification;
import com.innowise.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @CachePut(value = "users", key = "#result.id")
  public User createUser(User user) {
    checkEmailUniqueness(user.getEmail());
    return userRepository.save(user);
  }

  @Override
  @Cacheable(value = "users", key = "#id")
  @Transactional(readOnly = true)
  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("email", email));
  }

  @Override
  @Cacheable(value = "userWithCards", key = "#id")
  @Transactional(readOnly = true)
  public UserWithCardsDto getUserWithCardsById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    return userMapper.userToUserWithCardsDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> getAllUsers(String name, String surname, Pageable pageable) {
    return userRepository.findAll(
        UserSpecification.filterByNameAndSurname(name, surname), pageable);
  }

  @Override
  @CachePut(value = "users", key = "#id")
  @CacheEvict(value = "userWithCards", key = "#id")
  public User updateUser(Long id, User updatedUser) {

    User existingUser =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    if (updatedUser.getEmail() != null) {
      existingUser.setEmail(updatedUser.getEmail());
    }

    if (updatedUser.getName() != null) {
      existingUser.setName(updatedUser.getName());
    }
    if (updatedUser.getSurname() != null) {
      existingUser.setSurname(updatedUser.getSurname());
    }
    if (updatedUser.getBirthDate() != null) {
      existingUser.setBirthDate(updatedUser.getBirthDate());
    }

    return userRepository.save(existingUser);
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userWithCards", key = "#id")
      })
  public User updateUserStatus(Long id, Boolean active) {
    User existingUser =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    existingUser.setActive(active);
    return userRepository.save(existingUser);
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userWithCards", key = "#id")
      })
  public void activateUser(Long id) {
    updateUserStatus(id, true);
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userWithCards", key = "#id")
      })
  public void deactivateUser(Long id) {
    updateUserStatus(id, false);
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userWithCards", key = "#id")
      })
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }
    userRepository.deleteById(id);
  }

  private void checkEmailUniqueness(String email) {
    userRepository
        .findByEmail(email)
        .ifPresent(
            u -> {
              log.warn("User already exists with email: {}", email);
              throw UserAlreadyExistsException.withEmail(email);
            });
  }
}
