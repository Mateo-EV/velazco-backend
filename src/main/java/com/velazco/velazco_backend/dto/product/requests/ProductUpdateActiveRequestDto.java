package com.velazco.velazco_backend.dto.product.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateActiveRequestDto {

  @NotNull(message = "Active status must be provided")
  private Boolean active;

}
