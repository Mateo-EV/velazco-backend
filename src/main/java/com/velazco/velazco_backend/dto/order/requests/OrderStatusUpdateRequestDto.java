package com.velazco.velazco_backend.dto.order.requests;

import com.velazco.velazco_backend.entities.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequestDto {
    @NotNull
    private Order.OrderStatus status;
}
