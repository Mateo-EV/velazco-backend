package com.velazco.velazco_backend.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthLoginRequestDto {
  @NotBlank(message = "El email no puede estar vacío")
  @Email(message = "El email debe ser válido")
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacía")
  private String password;
}
