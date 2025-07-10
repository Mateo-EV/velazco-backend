package com.velazco.velazco_backend.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velazco.velazco_backend.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_backend.dto.auth.response.AuthLoginResponse;
import com.velazco.velazco_backend.entities.RefreshToken;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.exception.GeneralBadRequestException;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.security.jwt.JwtTokenProvider;
import com.velazco.velazco_backend.services.AuthService;
import com.velazco.velazco_backend.services.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;

  @Override
  @Transactional
  public AuthLoginResponse login(AuthLoginRequestDto request, HttpServletRequest httpRequest) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

    if (!encoder.matches(request.getPassword(), user.getPassword())) {
      throw new GeneralBadRequestException("Contraseña incorrecta");
    }

    if (!user.getActive()) {
      throw new GeneralBadRequestException("Usuario inactivo");
    }

    // Revocar todos los refresh tokens existentes del usuario para sesión única
    refreshTokenService.revokeAllUserTokens(user);

    // Generar nuevos tokens
    String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
    String deviceInfo = getDeviceInfo(httpRequest);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

    return AuthLoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())
        .build();
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {
    if (refreshToken != null && !refreshToken.isEmpty()) {
      refreshTokenService.revokeRefreshToken(refreshToken);
    }
  }

  private String getDeviceInfo(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    String ipAddress = getClientIpAddress(request);
    return String.format("IP: %s, UA: %s", ipAddress,
        userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 100)) : "Unknown");
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
