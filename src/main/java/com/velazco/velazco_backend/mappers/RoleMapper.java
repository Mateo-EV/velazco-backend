package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.roles.response.RoleDto;
import com.velazco.velazco_backend.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  @Mapping(target = "count", expression = "java((long) role.getUsers().size())")
  RoleDto toDto(Role role);

  List<RoleDto> toListDto(List<Role> roles);
}
