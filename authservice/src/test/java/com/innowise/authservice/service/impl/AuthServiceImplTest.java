package com.innowise.authservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.authservice.exception.InvalidRefreshTokenException;
import com.innowise.authservice.exception.TokenValidationException;
import com.innowise.authservice.exception.UserAlreadyExistsException;
import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.request.SignInRequest;
import com.innowise.authservice.model.dto.request.UserRequest;
import com.innowise.authservice.model.dto.request.ValidationTokenRequest;
import com.innowise.authservice.model.dto.response.TokenResponse;
import com.innowise.authservice.model.dto.response.ValidationTokenResponse;
import com.innowise.authservice.model.entity.User;
import com.innowise.authservice.model.entity.type.RoleType;
import com.innowise.authservice.repository.UserRepository;
import com.innowise.authservice.service.CustomUserDetailsService;
import com.innowise.authservice.util.JwtTokenUtil;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtTokenUtil jwtTokenUtil;
  @Mock private CustomUserDetailsService userDetailsService;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthServiceImpl authService;

  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@mail.ru");
    user.setUsername("yNohach");
    user.setPassword("password");
    user.setRole(RoleType.USER);

    user.setEnabled(true);
    user.setAccountNonExpired(true);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);

    this.userDetails = user;
  }

  @Test
  void login_Success() {
    SignInRequest request = new SignInRequest();
    request.setEmail("test@mail.ru");
    request.setPassword("password");

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of((User) userDetails));

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    when(jwtTokenUtil.generateToken(userDetails)).thenReturn("access-token");
    when(jwtTokenUtil.generateRefreshToken(userDetails)).thenReturn("refresh-token");

    TokenResponse response = authService.login(request);

    assertNotNull(response);
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());

    verify(userRepository).findByEmail("test@mail.ru");
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void refreshToken_Success() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("valid-refresh-token");

    when(jwtTokenUtil.isInvalid("valid-refresh-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-refresh-token")).thenReturn("test@mail.ru");
    when(userDetailsService.loadUserByUsername("test@mail.ru")).thenReturn(userDetails);
    when(jwtTokenUtil.generateToken(userDetails)).thenReturn("new-access-token");
    when(jwtTokenUtil.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");

    TokenResponse response = authService.refreshToken(request);

    assertNotNull(response);
    assertEquals("new-access-token", response.getAccessToken());
    verify(userDetailsService).loadUserByUsername("test@mail.ru");
  }

  @Test
  void refreshToken_ThrowsInvalidRefreshTokenException() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("invalid-token");

    when(jwtTokenUtil.isInvalid("invalid-token")).thenReturn(true);

    assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(request));
  }

  @Test
  void validateToken_Success() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("valid-token");

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token")).thenReturn("test@mail.ru");
    when(userDetailsService.loadUserByUsername("test@mail.ru")).thenReturn(userDetails);
    when(jwtTokenUtil.extractExpiration("valid-token")).thenReturn(new Date());

    ValidationTokenResponse response = authService.validateToken(request);

    assertTrue(response.isValid());
    assertEquals("test@mail.ru", response.getEmail());
    assertEquals("ROLE_USER", response.getRole());
  }

  @Test
  void validateToken_InvalidToken_ThrowsException() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("invalid");

    when(jwtTokenUtil.isInvalid("invalid")).thenReturn(true);

    assertThrows(TokenValidationException.class, () -> authService.validateToken(request));
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  void register_Success() {
    UserRequest request = new UserRequest();
    request.setEmail("test@mail.ru");
    request.setUsername("yNohach");
    request.setPassword("password123");

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    authService.register(request);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void register_UserAlreadyExists_ThrowsException() {
    UserRequest request = new UserRequest();
    request.setEmail("test@mail.ru");

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(new User()));

    assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    verify(userRepository, never()).save(any(User.class));
  }
}
