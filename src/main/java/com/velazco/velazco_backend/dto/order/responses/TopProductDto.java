package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDto {
    private String productName;
    private int totalQuantitySold;
    private BigDecimal totalRevenue;
}
