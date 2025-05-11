package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStartResponseDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AttendedByOrderStartResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProductOrderStartResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailOrderStartResponseDto {
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductOrderStartResponseDto product;
  }

  private Long id;
  private LocalDateTime date;
  private String clientName;
  private String status;
  private AttendedByOrderStartResponseDto attendedBy;
  private List<DetailOrderStartResponseDto> details;
}
