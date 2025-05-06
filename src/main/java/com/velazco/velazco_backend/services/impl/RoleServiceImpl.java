package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.RoleRepository;
import com.velazco.velazco_backend.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
  @SuppressWarnings("unused")
  private final RoleRepository roleRepository;

  public RoleServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }
}