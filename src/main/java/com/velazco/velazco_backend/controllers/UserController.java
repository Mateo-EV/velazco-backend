package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.services.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @SuppressWarnings("unused")
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
}