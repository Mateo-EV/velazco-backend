package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmSaleResponseDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SaleOrderConfirmSaleResponseDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CashierSaleOrderConfirmSaleResponseDto {
      private Long id;
      private String name;
    }

    private Long id;
    private LocalDateTime saleDate;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private CashierSaleOrderConfirmSaleResponseDto cashier;
  }

  private Long id;
  private LocalDateTime date;
  private String clientName;
  private String status;
}
