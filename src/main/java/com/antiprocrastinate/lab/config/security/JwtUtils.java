package com.antiprocrastinate.lab.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtils {
  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expirationMs}")
  private int jwtExpirationMs;

  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(CustomUserDetails userDetails) {
    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("id", userDetails.getId())
        .claim("roles", roles)
        .issuedAt(new Date())
        .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(getSignKey())
        .compact();
  }

  public Claims getClaimsFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean validateToken(String authToken) {
    try {
      getClaimsFromToken(authToken);
      return true;
    } catch (JwtException e) {
      log.error("Invalid JWT: {}", e.getMessage());
    }
    return false;
  }
}