package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_backend.dto.auth.request.RefreshTokenRequestDto;
import com.velazco.velazco_backend.dto.auth.response.AuthLoginResponse;
import com.velazco.velazco_backend.dto.auth.response.RefreshTokenResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

  AuthLoginResponse login(AuthLoginRequestDto request, HttpServletRequest httpRequest);

  RefreshTokenResponse refreshToken(RefreshTokenRequestDto request, HttpServletRequest httpRequest);

  void logout(String refreshToken);
}
