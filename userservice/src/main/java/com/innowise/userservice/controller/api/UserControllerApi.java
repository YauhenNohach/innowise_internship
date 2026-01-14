package com.innowise.userservice.controller.api;

import com.innowise.userservice.constant.ApiConstant;
import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RequestMapping(ApiConstant.USERS_BASE)
@Tag(name = "User Management", description = "API for user management")
public interface UserControllerApi {

  @Operation(summary = "Create new user", description = "Creates a new user in the system")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User successfully created",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid user data",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "409",
            description = "User with this email already exists",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PostMapping
  ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto);

  @Operation(summary = "Get user by ID", description = "Returns user information by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(schema = @Schema(implementation = UserWithCardsDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @GetMapping(ApiConstant.USER_ID_PATH)
  ResponseEntity<UserWithCardsDto> getUserById(@PathVariable Long id);

  @Operation(summary = "Get all users", description = "Returns paginated list of users")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  ResponseEntity<Page<UserDto>> getAllUsers(
      @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
      @Parameter(description = "Filter by surname") @RequestParam(required = false) String surname,
      @Parameter(description = "Filter by email") @RequestParam(required = false) String email,
      @Parameter(description = "Filter by active status") @RequestParam(required = false)
          Boolean active,
      @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable);

  @Operation(summary = "Update user", description = "Updates user information by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid user data",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PutMapping(ApiConstant.USER_ID_PATH)
  ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto);

  @Operation(
      summary = "Create payment card for user",
      description = "Creates a new payment card for specified user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Card successfully created",
            content = @Content(schema = @Schema(implementation = PaymentCardDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid card data",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PostMapping(ApiConstant.USER_CARDS_OPERATIONS)
  ResponseEntity<PaymentCardDto> createCard(
      @Parameter(description = "ID of the user to create a card for", required = true) @PathVariable
          Long userId,
      @Parameter(description = "Card data", required = true) @Valid @RequestBody
          PaymentCardDto cardDto);

  @Operation(
      summary = "Get cards by user ID",
      description = "Retrieves all payment cards for a specific user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cards successfully retrieved",
            content = @Content(schema = @Schema(implementation = PaymentCardDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @GetMapping(ApiConstant.USER_CARDS_OPERATIONS)
  ResponseEntity<List<PaymentCardDto>> getCardsByUserId(
      @Parameter(description = "ID of the user to retrieve cards for", required = true)
          @PathVariable
          Long userId);

  @Operation(summary = "Delete user", description = "Deletes user by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @DeleteMapping(ApiConstant.USER_ID_PATH)
  ResponseEntity<Void> deleteUser(@PathVariable Long id);

  @Operation(summary = "Activate user", description = "Activates user account by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User activated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PatchMapping(ApiConstant.ACTIVATE_USER)
  ResponseEntity<UserDto> activateUser(@PathVariable Long id);

  @Operation(summary = "Deactivate user", description = "Deactivates user account by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User deactivated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PatchMapping(ApiConstant.DEACTIVATE_USER)
  ResponseEntity<UserDto> deactivateUser(@PathVariable Long id);
}
