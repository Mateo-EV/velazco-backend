package com.velazco.velazco_backend.services.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.services.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

  private final RefreshTokenService refreshTokenService;

  // Ejecutar cada día a la medianoche
  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanupExpiredTokens() {
    log.info("Iniciando limpieza de refresh tokens expirados");
    refreshTokenService.deleteExpiredTokens();
    log.info("Limpieza de refresh tokens completada");
  }
}
