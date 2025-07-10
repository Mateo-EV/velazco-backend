package com.velazco.velazco_backend.security.jwt;

import java.security.Key;
import java.security.SecureRandom;
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

  private static final long ACCESS_TOKEN_EXPIRATION_MILLIS = 3600000; // 1 hour
  private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 30; // 30 days

  @Value("${jwt.secret}")
  private String jwtSecret;

  private Key secretKey;
  private SecureRandom secureRandom;

  @PostConstruct
  public void init() {
    byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
    this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    this.secureRandom = new SecureRandom();
  }

  public String generateAccessToken(String userId) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MILLIS);

    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(secretKey)
        .compact();
  }

  public String generateRefreshToken() {
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  public long getRefreshTokenExpirationDays() {
    return REFRESH_TOKEN_EXPIRATION_DAYS;
  }

  // Mantener compatibilidad con código existente
  public String generateToken(String userId) {
    return generateAccessToken(userId);
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
