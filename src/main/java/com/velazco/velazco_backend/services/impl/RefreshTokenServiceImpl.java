package com.velazco.velazco_backend.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velazco.velazco_backend.entities.RefreshToken;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.exception.GeneralBadRequestException;
import com.velazco.velazco_backend.repositories.RefreshTokenRepository;
import com.velazco.velazco_backend.security.jwt.JwtTokenProvider;
import com.velazco.velazco_backend.services.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  @Transactional
  public RefreshToken createRefreshToken(User user, String deviceInfo) {
    String tokenValue = jwtTokenProvider.generateRefreshToken();
    LocalDateTime expiresAt = LocalDateTime.now().plusDays(jwtTokenProvider.getRefreshTokenExpirationDays());

    RefreshToken refreshToken = new RefreshToken(tokenValue, user, expiresAt, deviceInfo);
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (!token.isValid()) {
      refreshTokenRepository.delete(token);
      throw new GeneralBadRequestException("El refresh token ha expirado o ha sido revocado");
    }
    return token;
  }

  @Override
  public RefreshToken findByToken(String token) {
    return refreshTokenRepository.findByTokenAndRevokedFalse(token)
        .orElseThrow(() -> new EntityNotFoundException("Refresh token no encontrado"));
  }

  @Override
  @Transactional
  public void revokeRefreshToken(String token) {
    refreshTokenRepository.revokeByToken(token);
  }

  @Override
  @Transactional
  public void revokeAllUserTokens(User user) {
    refreshTokenRepository.revokeAllByUser(user);
  }

  @Override
  @Transactional
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
  }
}
