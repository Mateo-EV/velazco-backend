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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final boolean isProduction = false;

  @Operation(summary = "Login endpoint", security = {})
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthLoginRequestDto request,
      HttpServletResponse response) {
    AuthLoginResponse loginResponse = authService.login(request);

    ResponseCookie cookie = ResponseCookie.from("velazco_token", loginResponse.getToken())
        .httpOnly(true)
        .secure(isProduction)
        .path("/")
        .maxAge(Duration.ofDays(1))
        .sameSite("Strict")
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok(Map.of("message", "Ingreso exitoso"));
  }

  @Operation(summary = "Logout endpoint", security = {})
  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("velazco_token", "")
        .httpOnly(true)
        .secure(isProduction)
        .path("/")
        .maxAge(0)
        .sameSite("Strict")
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok(Map.of("message", "Sesión cerrada"));
  }
}
