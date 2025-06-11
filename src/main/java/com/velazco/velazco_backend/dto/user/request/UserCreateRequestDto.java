package com.velazco.velazco_backend.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequestDto {
  @NotBlank(message = "El nombre no puede estar vacío")
  private String name;

  @NotEmpty(message = "El correo no puede estar vacío")
  @Email(message = "El correo debe ser válido")
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacía")
  @Min(value = 6, message = "La contraseña debe tener al menos 6 caracteres")
  private String password;

  @NotEmpty(message = "El estado activo no puede estar vacío")
  private Boolean active;

  @NotEmpty(message = "El ID del rol no puede estar vacío")
  private Long roleId;
}
