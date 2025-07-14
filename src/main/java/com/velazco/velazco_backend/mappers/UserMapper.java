package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.profile.response.ProfileDto;
import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.request.UserUpdateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserListResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;
import com.velazco.velazco_backend.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  List<UserListResponseDto> toUserListResponseDtoList(List<User> users);

  @Mapping(target = "role", source = "role.name")
  UserListResponseDto toUserListResponseDto(User user);

  @Mapping(target = "role", source = "role.name")
  ProfileDto toProfileDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sales", ignore = true)
  @Mapping(target = "attendedOrders", ignore = true)
  @Mapping(target = "dispatches", ignore = true)
  @Mapping(target = "assignedProductions", ignore = true)
  @Mapping(target = "responsibleProductions", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "role.id", source = "roleId")
  @Mapping(target = "refreshTokens", ignore = true)
  User toEntity(UserCreateRequestDto request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sales", ignore = true)
  @Mapping(target = "attendedOrders", ignore = true)
  @Mapping(target = "dispatches", ignore = true)
  @Mapping(target = "assignedProductions", ignore = true)
  @Mapping(target = "responsibleProductions", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "role.id", source = "roleId")
  @Mapping(target = "refreshTokens", ignore = true)
  User toEntity(UserUpdateRequestDto request);

  UserCreateResponseDto toUserCreateResponse(User user);

  UserUpdateResponseDto toUserUpdateResponse(User user);
}
