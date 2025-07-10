package com.velazco.velazco_backend.controllers;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velazco.velazco_backend.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_backend.dto.auth.response.AuthLoginResponse;
import com.velazco.velazco_backend.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Login endpoint", security = {})
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthLoginRequestDto request,
      HttpServletRequest httpRequest, HttpServletResponse response) {

    AuthLoginResponse loginResponse = authService.login(request, httpRequest);

    ResponseCookie accessTokenCookie = ResponseCookie.from("velazco_token", loginResponse.getAccessToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofHours(1))
        .sameSite("Strict")
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("velazco_refresh_token", loginResponse.getRefreshToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofDays(30))
        .sameSite("Strict")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    return ResponseEntity.ok(Map.of("message", "Ingreso exitoso"));
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {

    String refreshToken = getRefreshTokenFromCookies(request);
    authService.logout(refreshToken);

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

    return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
  }

  private String getRefreshTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("velazco_refresh_token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
