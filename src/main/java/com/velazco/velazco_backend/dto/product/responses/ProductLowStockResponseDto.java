package com.velazco.velazco_backend.dto.product.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductLowStockResponseDto {

    private Long count;

    private List<ProductData> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductData {
        private Long id;
        private String name;
        private Integer stock;
    }
}
