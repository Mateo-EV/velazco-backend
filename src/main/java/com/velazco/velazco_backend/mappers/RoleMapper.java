package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.velazco.velazco_backend.dto.roles.response.RoleDto;
import com.velazco.velazco_backend.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  List<RoleDto> toListDto(List<Role> role);
}
