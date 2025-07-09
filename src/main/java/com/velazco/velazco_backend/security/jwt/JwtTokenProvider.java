package com.velazco.velazco_backend.security.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "app.security.jwt.enabled", havingValue = "true", matchIfMissing = true)
public class JwtTokenProvider {
  private static final long EXPIRATION_MILLIS = 3600000; // 1 hour

  @Value("${jwt.secret}")
  private String jwtSecret;

  private Key secretKey;

  @PostConstruct
  public void init() {
    byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
    this.secretKey = Keys.hmacShaKeyFor(decodedKey);
  }

  public String generateToken(String userId) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + EXPIRATION_MILLIS);

    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(secretKey)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Long getUserIdFromToken(String token) {
    return Long.parseLong(parseClaims(token).getSubject());
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
