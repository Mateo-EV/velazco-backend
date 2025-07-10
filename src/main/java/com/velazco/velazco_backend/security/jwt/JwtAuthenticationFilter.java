package com.velazco.velazco_backend.security.jwt;

import java.io.IOException;
import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.velazco.velazco_backend.entities.RefreshToken;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.services.RefreshTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.jwt.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String accessToken = getAccessTokenFromRequest(request);
    String refreshToken = getRefreshTokenFromRequest(request);

    if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
      authenticateUser(accessToken);
    }

    else if (StringUtils.hasText(refreshToken)) {
      try {
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.verifyExpiration(refreshTokenEntity);

        User user = refreshTokenEntity.getUser();
        if (user.getActive()) {
          // Revocar el refresh token actual
          refreshTokenService.revokeRefreshToken(refreshToken);

          // Generar nuevos tokens
          String newAccessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
          String deviceInfo = getDeviceInfo(request);
          RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

          // Configurar nuevas cookies
          setTokenCookies(response, newAccessToken, newRefreshToken.getToken());

          // Autenticar con el nuevo token
          authenticateUser(newAccessToken);

          log.debug("Access token renovado automáticamente para usuario: {}", user.getEmail());
        }
      } catch (Exception e) {
        log.debug("Error al renovar token automáticamente: {}", e.getMessage());
        // Limpiar cookies inválidas
        clearTokenCookies(response);
      }
    }

    filterChain.doFilter(request, response);
  }

  private void authenticateUser(String accessToken) {
    try {
      Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

      if (user.getActive()) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.debug("Error al autenticar usuario: {}", e.getMessage());
    }
  }

  private String getAccessTokenFromRequest(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("velazco_token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    String bearer = request.getHeader("Authorization");
    if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }

    return null;
  }

  private String getRefreshTokenFromRequest(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("velazco_refresh_token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
    ResponseCookie accessTokenCookie = ResponseCookie.from("velazco_token", accessToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofHours(1))
        .sameSite("Strict")
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("velazco_refresh_token", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofDays(30))
        .sameSite("Strict")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
  }

  private void clearTokenCookies(HttpServletResponse response) {
    ResponseCookie accessTokenCookie = ResponseCookie.from("velazco_token", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ZERO)
        .sameSite("Strict")
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("velazco_refresh_token", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ZERO)
        .sameSite("Strict")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
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
