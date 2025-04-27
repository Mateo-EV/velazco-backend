package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.OrderRepository;
import com.velazco.velazco_backend.services.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }
}