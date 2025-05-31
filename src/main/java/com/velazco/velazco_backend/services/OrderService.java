package com.velazco.velazco_backend.services;

import org.springframework.data.domain.Pageable;

import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.User;

public interface OrderService {
    PaginatedResponseDto<OrderListResponseDto> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);

    Order getOrderById(Long id);

    OrderStartResponseDto startOrder(User user, OrderStartRequestDto orderRequest);

    OrderConfirmSaleResponseDto confirmSale(Long orderId, User cashier, String paymentMethod);

    void deleteCancelledOrdersOlderThanOneDay();

    void cancelOrder(Long orderId);
}
