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
import com.velazco.velazco_backend.services.EventPublisherService;
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
  private final EventPublisherService eventPublisherService;

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

    UserCreateResponseDto response = userMapper.toUserCreateResponse(user);
    eventPublisherService.publishUserCreated(response);

    return response;
  }

  @Override
  public UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto request) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    existing.setName(request.getName());
    existing.setEmail(request.getEmail());
    existing.setActive(request.getActive());

    Role role = roleRepository.findById(request.getRoleId())
        .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    existing.setRole(role);

    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      existing.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    UserUpdateResponseDto response = userMapper.toUserUpdateResponse(userRepository.save(existing));
    eventPublisherService.publishUserUpdated(response);

    return response;
  }

  @Override
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    try {
      UserCreateResponseDto deletedUser = userMapper.toUserCreateResponse(user);
      userRepository.delete(user);
      eventPublisherService.publishUserDeleted(deletedUser);
    } catch (Exception e) {
      throw new GeneralBadRequestException("No se puede eliminar el usuario porque está asociado a otros registros");
    }
  }
}