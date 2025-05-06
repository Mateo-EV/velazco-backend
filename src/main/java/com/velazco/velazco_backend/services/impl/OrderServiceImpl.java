package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.repositories.OrderRepository;
import com.velazco.velazco_backend.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public Page<Order> getAllOrders(Pageable pageable) {
    return orderRepository.findAll(pageable);
  }

}