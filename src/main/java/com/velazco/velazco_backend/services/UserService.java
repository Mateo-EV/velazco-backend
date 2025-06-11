package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;

public interface UserService {
  UserCreateResponseDto createUser(UserCreateRequestDto request);
}