package com.innowise.authservice.util;

import com.innowise.authservice.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @Value("${jwt.refresh-expiration}")
  private Long refreshExpiration;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    var roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    claims.put("roles", roles);

    if (userDetails instanceof User user) {
      claims.put("userId", user.getId());
    }

    return createToken(claims, userDetails.getUsername(), jwtExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return createToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
  }

  private String createToken(Map<String, Object> claims, String subject, Long expiration) {
    final long now = System.currentTimeMillis();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean isInvalid(String token) {
    try {
      extractAllClaims(token);
      return false;
    } catch (ExpiredJwtException e) {
      log.warn("JWT token is expired: {}", e.getMessage());
    } catch (MalformedJwtException | UnsupportedJwtException | SecurityException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error during JWT validation", e);
    }
    return true;
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }
}
