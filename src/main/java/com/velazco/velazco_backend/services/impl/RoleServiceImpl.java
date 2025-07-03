package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.dto.roles.response.RoleDto;
import com.velazco.velazco_backend.mappers.RoleMapper;
import com.velazco.velazco_backend.repositories.RoleRepository;
import com.velazco.velazco_backend.services.RoleService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;

  private final RoleMapper roleMapper;

  @Override
  public List<RoleDto> getAllRoles() {
    return roleMapper.toListDto(roleRepository.findAll());
  }
}