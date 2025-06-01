package com.velazco.velazco_backend.dto.order.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmDispatchResponseDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DispatchConfirmDispatchResponseDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDispatchConfirmDispatchResponseDto {
      private Long id;
      private String name;
    }

    private Long id;
    private LocalDateTime deliveryDate;
    private UserDispatchConfirmDispatchResponseDto dispatchedBy;
  }

  private Long id;
  private LocalDateTime date;
  private String clientName;
  private String status;
  private DispatchConfirmDispatchResponseDto dispatch;
}
