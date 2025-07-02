package com.velazco.velazco_backend.dto.profile.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

  private String name;
  private String email;
  private String role;
  private Boolean active;

}
