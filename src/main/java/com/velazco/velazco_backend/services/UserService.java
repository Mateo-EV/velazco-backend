package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.request.UserUpdateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserListResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;

public interface UserService {
  List<UserListResponseDto> getAllUsers();

  UserCreateResponseDto createUser(UserCreateRequestDto request);

  UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto request);

  void deleteUser(Long id);

}