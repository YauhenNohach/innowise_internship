package com.innowise.userservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.userservice.exception.UserAlreadyExistsException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void createUser_whenUserDoesNotExist_shouldReturnUser() {
    User user = new User();
    user.setEmail("test@mail.ru");

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(user);

    User createdUser = userService.createUser(user);

    assertNotNull(createdUser);
    assertEquals(user.getEmail(), createdUser.getEmail());
    verify(userRepository, times(1)).findByEmail(anyString());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void createUser_whenUserExists_shouldThrowException() {
    User user = new User();
    user.setEmail("test@mail.ru");

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
    verify(userRepository, times(1)).findByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void getUserById_whenUserExists_shouldReturnUser() {
    User user = new User();
    user.setId(1L);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

    User foundUser = userService.getUserById(1L);

    assertNotNull(foundUser);
    assertEquals(user.getId(), foundUser.getId());
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getUserById_whenUserDoesNotExist_shouldThrowException() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getUserByEmail_whenUserExists_shouldReturnUser() {
    User user = new User();
    user.setEmail("test@mail.ru");

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    User foundUser = userService.getUserByEmail("test@mail.ru");

    assertNotNull(foundUser);
    assertEquals(user.getEmail(), foundUser.getEmail());
    verify(userRepository, times(1)).findByEmail(anyString());
  }

  @Test
  void getUserByEmail_whenUserDoesNotExist_shouldThrowException() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("test@mail.ru"));
    verify(userRepository, times(1)).findByEmail(anyString());
  }

  @Test
  void getUserWithCardsById_whenUserExists_shouldReturnUserWithCardsDto() {
    User user = new User();
    user.setId(1L);
    UserWithCardsDto userWithCardsDto = new UserWithCardsDto();
    userWithCardsDto.setId(1L);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userMapper.userToUserWithCardsDto(any(User.class))).thenReturn(userWithCardsDto);

    UserWithCardsDto foundUser = userService.getUserWithCardsById(1L);

    assertNotNull(foundUser);
    assertEquals(user.getId(), foundUser.getId());
    verify(userRepository, times(1)).findById(anyLong());
    verify(userMapper, times(1)).userToUserWithCardsDto(any(User.class));
  }

  @Test
  void getUserWithCardsById_whenUserDoesNotExist_shouldThrowException() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserWithCardsById(1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(userMapper, never()).userToUserWithCardsDto(any(User.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void getAllUsers_shouldReturnPageOfUsers() {
    Page<User> userPage = new PageImpl<>(Collections.singletonList(new User()));
    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(userPage);

    Page<User> result = userService.getAllUsers(null, null, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void updateUser_whenUserExists_shouldReturnUpdatedUser() {
    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setEmail("old@mail.ru");

    User updatedUser = new User();
    updatedUser.setEmail("new@mail.ru");

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    User result = userService.updateUser(1L, updatedUser);

    assertNotNull(result);
    assertEquals(updatedUser.getEmail(), result.getEmail());
    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).findByEmail(anyString());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void updateUser_whenUserDoesNotExist_shouldThrowException() {
    User updatedUser = new User();
    updatedUser.setEmail("test@mail.ru");

    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updatedUser));
    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, never()).findByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_whenEmailExists_shouldThrowException() {
    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setEmail("old@mail.ru");

    User updatedUser = new User();
    updatedUser.setEmail("new@mail.ru");

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

    assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updatedUser));
    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).findByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void activateUser_shouldCallRepository() {
    doNothing().when(userRepository).updateUserStatus(anyLong(), eq(true));
    userService.activateUser(1L);
    verify(userRepository, times(1)).updateUserStatus(1L, true);
  }

  @Test
  void deactivateUser_shouldCallRepository() {
    doNothing().when(userRepository).updateUserStatus(anyLong(), eq(false));
    userService.deactivateUser(1L);
    verify(userRepository, times(1)).updateUserStatus(1L, false);
  }

  @Test
  void deleteUser_whenUserExists_shouldCallRepository() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(userRepository).deleteById(anyLong());

    userService.deleteUser(1L);

    verify(userRepository, times(1)).existsById(anyLong());
    verify(userRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void deleteUser_whenUserDoesNotExist_shouldThrowException() {
    when(userRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));

    verify(userRepository, times(1)).existsById(anyLong());
    verify(userRepository, never()).deleteById(anyLong());
  }
}
