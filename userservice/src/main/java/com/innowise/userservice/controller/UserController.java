package com.innowise.userservice.controller;

import com.innowise.userservice.constant.ApiConstant;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.USERS_BASE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "API for user management")
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;
  private final CardService cardService;
  private final PaymentCardMapper cardMapper;

  @Operation(summary = "Create new user", description = "Creates a new user in the system")
  @ApiResponse(
      responseCode = "201",
      description = "User successfully created",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid user data",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(
      responseCode = "409",
      description = "User with this email already exists",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PostMapping
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
    log.info("Creating user with email: {}", userDto.getEmail());
    User user = userMapper.userDtoToUser(userDto);
    User createdUser = userService.createUser(user);
    UserDto responseDto = userMapper.userToUserDto(createdUser);
    log.info("User created successfully with ID: {}", createdUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @Operation(summary = "Get user by ID", description = "Returns user information by ID")
  @ApiResponse(
      responseCode = "200",
      description = "User found",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @GetMapping(ApiConstant.USER_ID_PATH)
  public ResponseEntity<UserDto> getUserById(
      @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable("id")
          Long id) {

    log.info("Fetching user with ID: {}", id);
    User user = userService.getUserById(id);
    UserDto userDto = userMapper.userToUserDto(user);
    log.info("User fetched successfully with ID: {}", id);
    return ResponseEntity.ok(userDto);
  }

  @Operation(summary = "Get all users", description = "Returns paginated list of users")
  @ApiResponse(
      responseCode = "200",
      description = "Users retrieved successfully",
      content = @Content(schema = @Schema(implementation = Page.class)))
  @GetMapping
  public ResponseEntity<Page<UserDto>> getAllUsers(
      @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
      @Parameter(description = "Filter by surname") @RequestParam(required = false) String surname,
      @Parameter(description = "Filter by email") @RequestParam(required = false) String email,
      @Parameter(description = "Filter by active status") @RequestParam(required = false)
          Boolean active,
      @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {

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

  @Operation(summary = "Update user", description = "Updates user information by ID")
  @ApiResponse(
      responseCode = "200",
      description = "User updated successfully",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid user data",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PutMapping(ApiConstant.USER_ID_PATH)
  public ResponseEntity<UserDto> updateUser(
      @Parameter(description = "ID of the user to update", required = true) @PathVariable("id")
          Long id,
      @Parameter(description = "Updated user data", required = true) @Valid @RequestBody
          UserDto userDto) {

    log.info("Updating user with ID: {}", id);
    User user = userMapper.userDtoToUser(userDto);
    User updatedUser = userService.updateUser(id, user);
    UserDto responseDto = userMapper.userToUserDto(updatedUser);
    log.info("User updated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(
      summary = "Create payment card for user",
      description = "Creates a new payment card for specified user")
  @ApiResponse(
      responseCode = "201",
      description = "Card successfully created",
      content = @Content(schema = @Schema(implementation = PaymentCardDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid card data",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PostMapping(ApiConstant.USER_CARDS_OPERATIONS)
  public ResponseEntity<PaymentCardDto> createCard(
      @Parameter(description = "ID of the user to create a card for", required = true)
          @PathVariable("userId")
          Long userId,
      @Parameter(description = "Card data", required = true) @Valid @RequestBody
          PaymentCardDto cardDto) {

    log.info("Creating card for user ID: {}", userId);
    PaymentCard card = cardMapper.cardDtoToCard(cardDto);
    PaymentCard createdCard = cardService.createCard(card, userId);
    PaymentCardDto responseDto = cardMapper.cardToCardDto(createdCard);
    log.info("Card created successfully with number: {}", responseDto.getNumber());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @Operation(
      summary = "Get cards by user ID",
      description = "Retrieves all payment cards for a specific user")
  @ApiResponse(
      responseCode = "200",
      description = "Cards successfully retrieved",
      content =
          @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentCardDto.class))))
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @GetMapping(ApiConstant.USER_CARDS_OPERATIONS)
  public ResponseEntity<List<PaymentCardDto>> getCardsByUserId(
      @Parameter(description = "ID of the user to retrieve cards for", required = true)
          @PathVariable("userId")
          Long userId) {

    log.info("Fetching cards for user ID: {}", userId);
    List<PaymentCard> cards = cardService.getCardsByUserId(userId);
    List<PaymentCardDto> responseDtos = cards.stream().map(cardMapper::cardToCardDto).toList();
    log.info("Found {} cards for user ID: {}", responseDtos.size(), userId);
    return ResponseEntity.ok(responseDtos);
  }

  @Operation(summary = "Delete user", description = "Deletes user by ID")
  @ApiResponse(responseCode = "204", description = "User deleted successfully")
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @DeleteMapping(ApiConstant.USER_ID_PATH)
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "ID of the user to delete", required = true) @PathVariable("id")
          Long id) {

    log.info("Deleting user with ID: {}", id);
    userService.deleteUser(id);
    log.info("User deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Activate user", description = "Activates user account by ID")
  @ApiResponse(
      responseCode = "200",
      description = "User activated successfully",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PatchMapping(ApiConstant.ACTIVATE_USER)
  public ResponseEntity<UserDto> activateUser(
      @Parameter(description = "ID of the user to activate", required = true) @PathVariable("id")
          Long id) {

    log.info("Activating user with ID: {}", id);
    userService.activateUser(id);
    User user = userService.getUserById(id);
    UserDto responseDto = userMapper.userToUserDto(user);
    log.info("User activated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(summary = "Deactivate user", description = "Deactivates user account by ID")
  @ApiResponse(
      responseCode = "200",
      description = "User deactivated successfully",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(schema = @Schema(implementation = String.class)))
  @PatchMapping(ApiConstant.DEACTIVATE_USER)
  public ResponseEntity<UserDto> deactivateUser(
      @Parameter(description = "ID of the user to deactivate", required = true) @PathVariable("id")
          Long id) {

    log.info("Deactivating user with ID: {}", id);
    userService.deactivateUser(id);
    User user = userService.getUserById(id);
    UserDto responseDto = userMapper.userToUserDto(user);
    log.info("User deactivated successfully with ID: {}", id);
    return ResponseEntity.ok(responseDto);
  }
}
