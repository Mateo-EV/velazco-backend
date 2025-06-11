package com.velazco.velazco_backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sales", ignore = true)
  @Mapping(target = "attendedOrders", ignore = true)
  @Mapping(target = "dispatches", ignore = true)
  @Mapping(target = "assignedProductions", ignore = true)
  @Mapping(target = "responsibleProductions", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "role.id", source = "roleId")
  User toEntity(UserCreateRequestDto request);

  UserCreateResponseDto toUserCreateResponseDto(User user);
}
