package com.velazco.velazco_backend.dto.product.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateActiveResponseDto {
  private Long id;
  private Boolean active;
}
