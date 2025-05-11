package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto.ProductOrderStartResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponseDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendedByListResponseDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailsOrderResponseDto {
        private Integer quantity;
        private BigDecimal unitPrice;
        private ProductOrderStartResponseDto product;
    }

    private Long id;
    private LocalDateTime date;
    private String clientName;
    private String status;
    private AttendedByListResponseDto attendedBy;
    private List<DetailsOrderResponseDto> details;

}
