package com.velazco.velazco_backend.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_backend.dto.auth.response.AuthLoginResponse;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.exception.GeneralBadRequestException;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.security.jwt.JwtTokenProvider;
import com.velazco.velazco_backend.services.AuthService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  private final PasswordEncoder encoder;

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public AuthLoginResponse login(AuthLoginRequestDto request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

    if (!encoder.matches(request.getPassword(), user.getPassword())) {
      throw new GeneralBadRequestException("Contraseña incorrecta");
    }

    String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()));

    return new AuthLoginResponse(token);
  }
}
