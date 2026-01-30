package com.innowise.authservice.model.dto.response;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidationTokenResponse {
  private boolean valid;
  private String email;
  private String role;
  private Date expiresAt;
}
