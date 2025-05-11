package com.velazco.velazco_backend.dto.order.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmSaleRequestDto {
  @NotBlank
  private String paymentMethod;
}
