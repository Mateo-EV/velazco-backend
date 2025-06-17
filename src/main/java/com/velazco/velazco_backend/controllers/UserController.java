package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.request.UserUpdateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;
import com.velazco.velazco_backend.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @PostMapping
  ResponseEntity<UserCreateResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto request) {
    UserCreateResponseDto response = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  ResponseEntity<UserUpdateResponseDto> updateUser(@PathVariable Long id,
      @Valid @RequestBody UserUpdateRequestDto request) {
    UserUpdateResponseDto response = userService.updateUser(id, request);
    return ResponseEntity.ok(response);
  }
}