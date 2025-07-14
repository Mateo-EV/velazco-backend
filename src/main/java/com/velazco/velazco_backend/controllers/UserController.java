package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.request.UserUpdateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserListResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;
import com.velazco.velazco_backend.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping
  public ResponseEntity<List<UserListResponseDto>> getAllUsers() {
    List<UserListResponseDto> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @PreAuthorize("hasRole('Administrador')")
  @PostMapping
  ResponseEntity<UserCreateResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto request) {
    UserCreateResponseDto response = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PreAuthorize("hasRole('Administrador')")
  @PutMapping("/{id}")
  ResponseEntity<UserUpdateResponseDto> updateUser(@PathVariable Long id,
      @Valid @RequestBody UserUpdateRequestDto request) {
    UserUpdateResponseDto response = userService.updateUser(id, request);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('Administrador')")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}