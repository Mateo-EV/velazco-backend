package com.velazco.velazco_backend.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDto {
    private Long id; 
    private String name;
    private String email;
    private String role;
    private Boolean active;
}
