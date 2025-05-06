package com.velazco.velazco_backend.dto.product.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    // private String image;

    @NotNull
    private Boolean active;

    @NotNull
    private Long categoryId;
}
