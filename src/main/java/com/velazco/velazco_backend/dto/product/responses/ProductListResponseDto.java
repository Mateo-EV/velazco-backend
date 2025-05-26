package com.velazco.velazco_backend.dto.product.responses;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponseDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static public class CategoryProductListResponseDto {
        private Long id;
        private String name;
    }

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Boolean active;

    private CategoryProductListResponseDto category;
}
