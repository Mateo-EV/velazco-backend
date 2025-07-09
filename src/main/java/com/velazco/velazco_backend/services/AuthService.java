package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_backend.dto.auth.response.AuthLoginResponse;

public interface AuthService {
  AuthLoginResponse login(AuthLoginRequestDto request);
}
