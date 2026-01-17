package com.innowise.userservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  public boolean isSelf(Long userId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    String authUserId = authentication.getName();
    return String.valueOf(userId).equals(authUserId);
  }

  public boolean hasAdminRole(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
  }
}
