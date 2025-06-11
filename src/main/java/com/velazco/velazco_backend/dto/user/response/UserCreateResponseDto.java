package com.velazco.velazco_backend.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateResponseDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RoleUserCreateResponseDto {
    private Long id;
    private String name;
  }

  private Long id;
  private String name;
  private String email;
  private Boolean active;
  private RoleUserCreateResponseDto role;
}
