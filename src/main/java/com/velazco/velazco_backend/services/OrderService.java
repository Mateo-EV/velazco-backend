package com.velazco.velazco_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.velazco.velazco_backend.entities.Order;

public interface OrderService {
    Page<Order> getAllOrders(Pageable pageable);
}
