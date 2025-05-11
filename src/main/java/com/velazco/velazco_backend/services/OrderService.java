package com.velazco.velazco_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.User;

public interface OrderService {
    Page<Order> getAllOrders(Pageable pageable);

    OrderStartResponseDto startOrder(User user, OrderStartRequestDto orderRequest);
}
