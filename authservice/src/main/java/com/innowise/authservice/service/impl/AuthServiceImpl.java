package com.innowise.authservice.service.impl;

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
import com.innowise.authservice.service.AuthService;
import com.innowise.authservice.service.CustomUserDetailsService;
import com.innowise.authservice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final CustomUserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void register(UserRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw UserAlreadyExistsException.withEmail(request.getEmail());
    }

    User user = new User();
    user.setEmail(request.getEmail());
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(RoleType.USER);

    userRepository.save(user);
    log.info("Registered new user with email: {}", user.getEmail());
  }

  @Override
  public TokenResponse login(SignInRequest request) {
    userRepository
        .findByEmail(request.getEmail())
        .orElseThrow(
            () -> {
              log.error("Invalid email");
              return new UserNotFoundException("email", request.getEmail());
            });

    UserDetails userDetails;
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      userDetails = (UserDetails) authentication.getPrincipal();
    } catch (AuthenticationException e) {
      throw new AuthenticationFailedException("Invalid email or password");
    }

    return generateTokenResponse(userDetails);
  }

  @Override
  public TokenResponse refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();

    if (jwtTokenUtil.isInvalid(refreshToken)) {
      throw new InvalidRefreshTokenException();
    }

    String username = jwtTokenUtil.extractUsername(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    log.debug("Refreshing token for user: {}", username);
    return generateTokenResponse(userDetails);
  }

  @Override
  public ValidationTokenResponse validateToken(ValidationTokenRequest request) {
    String token = request.getToken();

    if (jwtTokenUtil.isInvalid(token)) {
      log.warn("Token validation failed for token: {}", token);
      throw new TokenValidationException("Token is invalid or expired");
    }

    try {
      String username = jwtTokenUtil.extractUsername(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      return ValidationTokenResponse.builder()
          .valid(true)
          .email(username)
          .role(extractRole(userDetails))
          .expiresAt(jwtTokenUtil.extractExpiration(token))
          .build();

    } catch (Exception e) {
      log.error("Unexpected error during token validation", e);
      throw new TokenValidationException("Could not process token: " + e.getMessage());
    }
  }

  private TokenResponse generateTokenResponse(UserDetails userDetails) {
    return new TokenResponse(
        jwtTokenUtil.generateToken(userDetails), jwtTokenUtil.generateRefreshToken(userDetails));
  }

  private String extractRole(UserDetails userDetails) {
    return userDetails.getAuthorities().stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse("ROLE_USER");
  }
}
