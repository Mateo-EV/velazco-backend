package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DailySaleResponseDto {
    private LocalDate date;
    private BigDecimal totalSales;

    private List<ProductSold> products;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ProductSold {
        private String productName;
        private Integer quantitySold;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}