package com.velazco.velazco_backend.dto.order.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveredOrderResponseDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendedByDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveredByDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
        private Integer quantity;
        private BigDecimal unitPrice;
        private ProductDto product;
    }

    private Long id;
    private LocalDateTime date;
    private String clientName;
    private String status;

    private AttendedByDto attendedBy;
    private DeliveredByDto deliveredBy;
    private LocalDateTime deliveryDate;

    private List<OrderDetailDto> details;
}
