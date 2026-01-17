package com.innowise.authservice.model.entity;

import com.innowise.authservice.model.entity.type.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "auth_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", unique = true, nullable = false, length = 50)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private RoleType role;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled;

  @Column(name = "account_non_expired", nullable = false)
  private Boolean accountNonExpired;

  @Column(name = "account_non_locked", nullable = false)
  private Boolean accountNonLocked;

  @Column(name = "credentials_non_expired", nullable = false)
  private Boolean credentialsNonExpired;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (enabled == null) {
      enabled = true;
    }
    if (accountNonExpired == null) {
      accountNonExpired = true;
    }
    if (accountNonLocked == null) {
      accountNonLocked = true;
    }
    if (credentialsNonExpired == null) {
      credentialsNonExpired = true;
    }
    if (role == null) {
      role = RoleType.USER;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
