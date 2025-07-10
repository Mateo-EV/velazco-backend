package com.velazco.velazco_backend.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.velazco.velazco_backend.services.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

  private final RefreshTokenService refreshTokenService;

  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanupExpiredTokens() {
    log.info("Iniciando limpieza de refresh tokens expirados");
    refreshTokenService.deleteExpiredTokens();
    log.info("Limpieza de refresh tokens completada");
  }
}
