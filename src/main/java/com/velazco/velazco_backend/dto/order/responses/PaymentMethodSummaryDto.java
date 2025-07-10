package com.velazco.velazco_backend.dto.order.responses;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodSummaryDto {
    private String paymentMethod;
    private BigDecimal totalSales;
    private double percentage;
}
