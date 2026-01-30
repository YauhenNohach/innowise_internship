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

import com.innowise.authservice.exception.AuthenticationFailedException;
import com.innowise.authservice.exception.InvalidRefreshTokenException;
import com.innowise.authservice.exception.TokenValidationException;
import com.innowise.authservice.exception.UserAlreadyExistsException;
import com.innowise.authservice.exception.UserNotFoundException;
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
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
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

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));

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
    verify(jwtTokenUtil).generateToken(userDetails);
    verify(jwtTokenUtil).generateRefreshToken(userDetails);
  }

  @Test
  void login_UserNotFound_ThrowsException() {
    SignInRequest request = new SignInRequest();
    request.setEmail("notfound@mail.ru");
    request.setPassword("password");

    when(userRepository.findByEmail("notfound@mail.ru")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> authService.login(request));

    verify(userRepository).findByEmail("notfound@mail.ru");
    verify(authenticationManager, never())
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void login_AuthenticationFailed_ThrowsException() {
    SignInRequest request = new SignInRequest();
    request.setEmail("test@mail.ru");
    request.setPassword("wrongpassword");

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    assertThrows(AuthenticationFailedException.class, () -> authService.login(request));

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
    assertEquals("new-refresh-token", response.getRefreshToken());

    verify(jwtTokenUtil).isInvalid("valid-refresh-token");
    verify(jwtTokenUtil).extractUsername("valid-refresh-token");
    verify(userDetailsService).loadUserByUsername("test@mail.ru");
    verify(jwtTokenUtil).generateToken(userDetails);
    verify(jwtTokenUtil).generateRefreshToken(userDetails);
  }

  @Test
  void refreshToken_ThrowsInvalidRefreshTokenException() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("invalid-token");

    when(jwtTokenUtil.isInvalid("invalid-token")).thenReturn(true);

    assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(request));

    verify(jwtTokenUtil).isInvalid("invalid-token");
    verify(jwtTokenUtil, never()).extractUsername(anyString());
  }

  @Test
  void refreshToken_UserNotFound_ThrowsException() {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("valid-token");

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token")).thenReturn("notfound@mail.ru");
    when(userDetailsService.loadUserByUsername("notfound@mail.ru"))
        .thenThrow(new UsernameNotFoundException("User not found"));

    assertThrows(UsernameNotFoundException.class, () -> authService.refreshToken(request));

    verify(jwtTokenUtil).isInvalid("valid-token");
    verify(jwtTokenUtil).extractUsername("valid-token");
    verify(userDetailsService).loadUserByUsername("notfound@mail.ru");
  }

  @Test
  void validateToken_Success() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("valid-token");
    Date expirationDate = new Date(System.currentTimeMillis() + 3600000);

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token")).thenReturn("test@mail.ru");
    when(userDetailsService.loadUserByUsername("test@mail.ru")).thenReturn(userDetails);
    when(jwtTokenUtil.extractExpiration("valid-token")).thenReturn(expirationDate);

    ValidationTokenResponse response = authService.validateToken(request);

    assertNotNull(response);
    assertTrue(response.isValid());
    assertEquals("test@mail.ru", response.getEmail());
    assertEquals("ROLE_USER", response.getRole());
    assertEquals(expirationDate, response.getExpiresAt());

    verify(jwtTokenUtil).isInvalid("valid-token");
    verify(jwtTokenUtil).extractUsername("valid-token");
    verify(userDetailsService).loadUserByUsername("test@mail.ru");
    verify(jwtTokenUtil).extractExpiration("valid-token");
  }

  @Test
  void validateToken_WithEmptyAuthorities_ReturnsDefaultRole() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("valid-token");
    Date expirationDate = new Date(System.currentTimeMillis() + 3600000);

    UserDetails customUserDetails =
        new org.springframework.security.core.userdetails.User(
            "test@mail.ru", "password", Collections.emptyList());

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token")).thenReturn("test@mail.ru");
    when(userDetailsService.loadUserByUsername("test@mail.ru")).thenReturn(customUserDetails);
    when(jwtTokenUtil.extractExpiration("valid-token")).thenReturn(expirationDate);

    ValidationTokenResponse response = authService.validateToken(request);

    assertNotNull(response);
    assertTrue(response.isValid());
    assertEquals("test@mail.ru", response.getEmail());
    assertEquals("ROLE_USER", response.getRole()); // Default role when no authorities

    verify(jwtTokenUtil).isInvalid("valid-token");
    verify(jwtTokenUtil).extractUsername("valid-token");
    verify(userDetailsService).loadUserByUsername("test@mail.ru");
  }

  @Test
  void validateToken_InvalidToken_ThrowsException() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("invalid");

    when(jwtTokenUtil.isInvalid("invalid")).thenReturn(true);

    assertThrows(TokenValidationException.class, () -> authService.validateToken(request));

    verify(jwtTokenUtil).isInvalid("invalid");
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  void validateToken_ExtractUsernameThrowsException_ThrowsTokenValidationException() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("valid-token");

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token"))
        .thenThrow(new RuntimeException("Token parsing error"));

    assertThrows(TokenValidationException.class, () -> authService.validateToken(request));

    verify(jwtTokenUtil).isInvalid("valid-token");
    verify(jwtTokenUtil).extractUsername("valid-token");
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  void validateToken_UserNotFound_ThrowsTokenValidationException() {
    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("valid-token");

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token")).thenReturn("notfound@mail.ru");
    when(userDetailsService.loadUserByUsername("notfound@mail.ru"))
        .thenThrow(new UsernameNotFoundException("User not found"));

    assertThrows(TokenValidationException.class, () -> authService.validateToken(request));

    verify(jwtTokenUtil).isInvalid("valid-token");
    verify(jwtTokenUtil).extractUsername("valid-token");
    verify(userDetailsService).loadUserByUsername("notfound@mail.ru");
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

    verify(userRepository, times(1)).findByEmail("test@mail.ru");
    verify(passwordEncoder).encode("password123");
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void register_UserAlreadyExists_ThrowsException() {
    UserRequest request = new UserRequest();
    request.setEmail("test@mail.ru");
    request.setUsername("yNohach");
    request.setPassword("password123");

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(new User()));

    assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

    verify(userRepository).findByEmail("test@mail.ru");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void register_WithNullFields_ThrowsException() {
    UserRequest request = new UserRequest();
    request.setEmail(null);
    request.setUsername(null);
    request.setPassword(null);

    when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> authService.register(request));
  }

  @Test
  void generateTokenResponse_Success() {
    when(jwtTokenUtil.generateToken(userDetails)).thenReturn("access-token");
    when(jwtTokenUtil.generateRefreshToken(userDetails)).thenReturn("refresh-token");

    SignInRequest request = new SignInRequest();
    request.setEmail("test@mail.ru");
    request.setPassword("password");

    when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    TokenResponse response = authService.login(request);

    assertNotNull(response);
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());
  }

  @Test
  void extractRole_WithAuthorities_ReturnsAuthority() {
    UserDetails userWithAuthorities =
        new org.springframework.security.core.userdetails.User(
            "test@mail.ru",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

    ValidationTokenRequest request = new ValidationTokenRequest();
    request.setToken("valid-token");
    Date expirationDate = new Date(System.currentTimeMillis() + 3600000);

    when(jwtTokenUtil.isInvalid("valid-token")).thenReturn(false);
    when(jwtTokenUtil.extractUsername("valid-token")).thenReturn("test@mail.ru");
    when(userDetailsService.loadUserByUsername("test@mail.ru")).thenReturn(userWithAuthorities);
    when(jwtTokenUtil.extractExpiration("valid-token")).thenReturn(expirationDate);

    ValidationTokenResponse response = authService.validateToken(request);

    assertNotNull(response);
    assertEquals("ROLE_ADMIN", response.getRole());
  }
}
