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
public class ProductCreateResponseDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryProductCreateResponseDto {
        private Long id;
        private String name;
    }

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;

    private String image;

    private Boolean active;
    private CategoryProductCreateResponseDto category;
}
