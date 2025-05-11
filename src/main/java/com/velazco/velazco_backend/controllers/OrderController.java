package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.OrderMapper;
import com.velazco.velazco_backend.services.OrderService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;
  private final OrderMapper orderMapper;

  public OrderController(OrderService orderService, OrderMapper orderMapper) {
    this.orderService = orderService;
    this.orderMapper = orderMapper;
  }

  @GetMapping
  public ResponseEntity<PaginatedResponseDto<OrderListResponseDto>> getAllOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orderPage = orderService.getAllOrders(pageable);

    PaginatedResponseDto<OrderListResponseDto> response = PaginatedResponseDto.<OrderListResponseDto>builder()
        .content(orderMapper.toListResponse(orderPage.getContent()))
        .currentPage(orderPage.getNumber())
        .totalItems(orderPage.getTotalElements())
        .totalPages(orderPage.getTotalPages())
        .build();

    return ResponseEntity.ok(response);
  }

  @PostMapping("/start")
  public ResponseEntity<OrderStartResponseDto> startOrder(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody OrderStartRequestDto orderRequest) {
    OrderStartResponseDto responseDto = orderService.startOrder(user, orderRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }
}