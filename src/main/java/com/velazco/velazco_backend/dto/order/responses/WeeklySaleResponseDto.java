package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklySaleResponseDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSales;
    private int salesCount;
    private List<DeliveredOrder> orders;

    @Data
    @Builder
    public static class DeliveredOrder {
        private Long orderId;
        private LocalDate deliveryDate;
        private String dayOfWeek;
        private BigDecimal orderTotal;
        private List<ProductSold> products;
    }

    @Data
    @Builder
    public static class ProductSold {
        private String productName;
        private int quantitySold;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
