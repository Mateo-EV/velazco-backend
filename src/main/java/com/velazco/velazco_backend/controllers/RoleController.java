package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.services.RoleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
  @SuppressWarnings("unused")
  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }
}