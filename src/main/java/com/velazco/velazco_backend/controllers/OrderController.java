package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderConfirmSaleRequestDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.User;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<PaginatedResponseDto<OrderListResponseDto>> getOrdersByStatus(
      @PathVariable String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Order.OrderStatus orderStatus;
    try {
      orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(orderService.getOrdersByStatus(orderStatus, pageable));
  }

  @PostMapping("/start")
  public ResponseEntity<OrderStartResponseDto> startOrder(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody OrderStartRequestDto orderRequest) {

    OrderStartResponseDto responseDto = orderService.startOrder(user, orderRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @PostMapping("/{id}/confirm-sale")
  public ResponseEntity<OrderConfirmSaleResponseDto> confirmSale(
      @PathVariable Long id,
      @AuthenticationPrincipal User cashier,
      @Valid @RequestBody OrderConfirmSaleRequestDto requestDto) {

    OrderConfirmSaleResponseDto responseDto = orderService.confirmSale(id, cashier, requestDto.getPaymentMethod());

    return ResponseEntity.ok(responseDto);
  }

  @PutMapping("/{orderId}/cancel")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {

    orderService.cancelOrder(orderId);

    return ResponseEntity.noContent().build();
  }

}
