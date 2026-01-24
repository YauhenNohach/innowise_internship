package com.innowise.userservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.BaseIntegrationTest;
import com.innowise.userservice.model.dto.StatusUpdateDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setName("test");
    user.setSurname("test");
    user.setBirthDate(LocalDate.of(2000, 1, 1));
    user.setEmail("test@mail.ru");
    user.setActive(true);
    user = userRepository.save(user);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_whenValidUser_shouldReturnCreated() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("newUser");
    userDto.setSurname("newUser");
    userDto.setBirthDate(LocalDate.of(2000, 2, 2));
    userDto.setEmail("newuser@mail.ru");

    mockMvc
        .perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("newUser"))
        .andExpect(jsonPath("$.email").value("newuser@mail.ru"));
  }

  @Test
  void getUserById_whenUserExists_shouldReturnUser() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/{id}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.name").value(user.getName()));
  }

  @Test
  void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
    mockMvc.perform(get("/api/v1/users/{id}", 999L)).andExpect(status().isNotFound());
  }

  @Test
  void getAllUsers_shouldReturnPageOfUsers() throws Exception {
    mockMvc
        .perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(user.getId()));
  }

  @Test
  void getAllUsers_withNameFilter_shouldReturnFilteredUsers() throws Exception {
    mockMvc
        .perform(get("/api/v1/users").param("name", "test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("test"));
  }

  @Test
  void getAllUsers_withSurnameFilter_shouldReturnFilteredUsers() throws Exception {
    mockMvc
        .perform(get("/api/v1/users").param("surname", "test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].surname").value("test"));
  }

  @Test
  void getAllUsers_withBothFilters_shouldReturnFilteredUsers() throws Exception {
    mockMvc
        .perform(get("/api/v1/users").param("name", "test").param("surname", "test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("test"))
        .andExpect(jsonPath("$.content[0].surname").value("test"));
  }

  @Test
  void getAllUsers_withNonExistentNameFilter_shouldReturnEmpty() throws Exception {
    mockMvc
        .perform(get("/api/v1/users").param("name", "nonexistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0));
  }

  @Test
  void updateUser_whenValidUpdate_shouldReturnOk() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("test");
    userDto.setSurname("test");
    userDto.setEmail("test@mail.ru");
    userDto.setBirthDate(LocalDate.of(2000, 1, 1));
    userDto.setActive(true);

    mockMvc
        .perform(
            put("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("test"));
  }

  @Test
  void deleteUser_shouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/users/{id}", user.getId())).andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/users/{id}", user.getId())).andExpect(status().isNotFound());
  }

  @Test
  void updateUserStatus_activateUser_shouldReturnOk() throws Exception {
    user.setActive(false);

    StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
    statusUpdateDto.setActive(true);

    mockMvc
        .perform(
            patch("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  void updateUserStatus_deactivateUser_shouldReturnOk() throws Exception {
    StatusUpdateDto statusUpdateDto = new StatusUpdateDto();
    statusUpdateDto.setActive(false);

    mockMvc
        .perform(
            patch("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  void updateUserStatus_withInvalidData_shouldReturnBadRequest() throws Exception {
    String invalidJson = "{}";

    mockMvc
        .perform(
            patch("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andExpect(status().isBadRequest());
  }
}
