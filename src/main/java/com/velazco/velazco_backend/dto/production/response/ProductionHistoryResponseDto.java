package com.velazco.velazco_backend.dto.production.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionHistoryResponseDto {
    private String orderNumber;
    private LocalDate date;
    private String responsible;
    private String status;
    private List<ProductDetail> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetail {
        private String productName;
        private int quantity;
    }
}
