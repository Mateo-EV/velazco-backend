package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.request.UserUpdateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserListResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;
import com.velazco.velazco_backend.entities.Role;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.exception.GeneralBadRequestException;
import com.velazco.velazco_backend.mappers.UserMapper;
import com.velazco.velazco_backend.repositories.RoleRepository;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  private final UserMapper userMapper;

  private final PasswordEncoder passwordEncoder;

  @Override
  public List<UserListResponseDto> getAllUsers() {
    List<User> users = userRepository.findAll();
    return userMapper.toUserListResponseDtoList(users);
  }

  @Override
  public UserCreateResponseDto createUser(UserCreateRequestDto request) {
    User user = userMapper.toEntity(request);

    Role role = roleRepository.findById(request.getRoleId())
        .orElseThrow(() -> new EntityNotFoundException("Role not found"));

    user.setRole(role);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    return userMapper.toUserCreateResponse(user);
  }

  @Override
  public UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto request) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    User user = userMapper.toEntity(request);
    user.setId(existing.getId());

    Role role = roleRepository.findById(request.getRoleId())
        .orElseThrow(() -> new EntityNotFoundException("Role not found"));

    user.setRole(role);

    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    return userMapper.toUserUpdateResponse(userRepository.save(user));
  }

  @Override
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    try {
      userRepository.delete(user);
    } catch (Exception e) {
      throw new GeneralBadRequestException("No se puede eliminar el usuario porque está asociado a otros registros");
    }
  }
}