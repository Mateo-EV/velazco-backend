package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.entities.Role;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.UserMapper;
import com.velazco.velazco_backend.repositories.RoleRepository;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  private final UserMapper userMapper;

  @Override
  public UserCreateResponseDto createUser(UserCreateRequestDto request) {
    User user = userMapper.toEntity(request);

    Role role = roleRepository.findById(request.getRoleId())
        .orElseThrow(() -> new EntityNotFoundException("Role not found"));

    user.setRole(role);
    userRepository.save(user);

    return userMapper.toUserCreateResponseDto(user);
  }
}