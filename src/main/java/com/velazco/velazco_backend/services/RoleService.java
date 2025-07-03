package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.roles.response.RoleDto;

public interface RoleService {
  List<RoleDto> getAllRoles();
}