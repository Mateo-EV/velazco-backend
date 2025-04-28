package com.velazco.velazco_backend.dto.product.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateActiveResponseDto {
  private Long id;
  private Boolean active;
}
