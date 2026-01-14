package com.innowise.userservice.controller;

import com.innowise.userservice.controller.api.UserControllerApi;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserControllerApi {

  private final UserService userService;
  private final UserMapper userMapper;
  private final CardService cardService;
  private final PaymentCardMapper cardMapper;

  @Override
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
    log.info("Creating user with email: {}", userDto.getEmail());
    User user = userMapper.userDtoToUser(userDto);
    User createdUser = userService.createUser(user);
    UserDto responseDto = userMapper.userToUserDto(createdUser);
    log.info("User created successfully with ID: {}", createdUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @Override
  public ResponseEntity<UserWithCardsDto> getUserById(@PathVariable Long id) {
    log.info("Fetching user with ID: {}", id);
    UserWithCardsDto user = userService.getUserWithCardsById(id);
    log.info("User fetched successfully with ID: {}", id);
    return ResponseEntity.ok(user);
  }

  @Override
  public ResponseEntity<Page<UserDto>> getAllUsers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String surname,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) Boolean active,
      Pageable pageable) {

    log.info(
        "Fetching users with filters - name: {}, surname: {}, email: {}, active: {}",
        name,
        surname,
        email,
        active);

    Page<User> users = userService.getAllUsers(name, surname, pageable);
    Page<UserDto> userDtos = users.map(userMapper::userToUserDto);
    log.info("Found {} users", users.getTotalElements());

    return ResponseEntity.ok(userDtos);
  }

  @Override
  public ResponseEntity<UserDto> updateUser(
      @PathVariable Long id, @Valid @RequestBody UserDto userDto) {
    log.info("Updating user with ID: {}", id);
    User user = userMapper.userDtoToUser(userDto);
    User updatedUser = userService.updateUser(id, user);
    UserDto responseDto = userMapper.userToUserDto(updatedUser);
    log.info("User updated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Override
  public ResponseEntity<PaymentCardDto> createCard(
      @PathVariable Long userId, @Valid @RequestBody PaymentCardDto cardDto) {
    log.info("Creating card for user ID: {}", userId);
    PaymentCard card = cardMapper.cardDtoToCard(cardDto);
    PaymentCard createdCard = cardService.createCard(card, userId);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(createdCard);
    log.info("Card created successfully with number: {}", responseDto.getNumber());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @Override
  public ResponseEntity<List<PaymentCardDto>> getCardsByUserId(@PathVariable Long userId) {
    log.info("Fetching cards for user ID: {}", userId);
    List<PaymentCard> cards = cardService.getCardsByUserId(userId);
    List<PaymentCardDto> responseDtos = cards.stream().map(cardMapper::cardToCardDto).toList();
    log.info("Found {} cards for user ID: {}", responseDtos.size(), userId);
    return ResponseEntity.ok(responseDtos);
  }

  @Override
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    log.info("Deleting user with ID: {}", id);
    userService.deleteUser(id);
    log.info("User deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<UserDto> activateUser(@PathVariable Long id) {
    log.info("Activating user with ID: {}", id);
    userService.activateUser(id);
    User user = userService.getUserById(id);
    UserDto responseDto = userMapper.userToUserDto(user);
    log.info("User activated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Override
  public ResponseEntity<UserDto> deactivateUser(@PathVariable Long id) {
    log.info("Deactivating user with ID: {}", id);
    userService.deactivateUser(id);
    User user = userService.getUserById(id);
    UserDto responseDto = userMapper.userToUserDto(user);
    log.info("User deactivated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }
}
