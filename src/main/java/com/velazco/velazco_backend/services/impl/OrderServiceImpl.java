package com.velazco.velazco_backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.OrderDetail;
import com.velazco.velazco_backend.entities.OrderDetailId;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Sale;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.OrderMapper;
import com.velazco.velazco_backend.repositories.OrderRepository;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.repositories.SaleRepository;
import com.velazco.velazco_backend.services.OrderService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final SaleRepository saleRepository;
  private final ProductRepository productRepository;

  private final OrderMapper orderMapper;

  @Override
  public PaginatedResponseDto<OrderListResponseDto> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
    Page<Order> orderPage = orderRepository.findByStatus(status, pageable);

    return PaginatedResponseDto.<OrderListResponseDto>builder()
        .content(orderMapper.toListResponse(orderPage.getContent()))
        .currentPage(orderPage.getNumber())
        .totalItems(orderPage.getTotalElements())
        .totalPages(orderPage.getTotalPages())
        .build();
  }

  @Override
  public Order getOrderById(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Order not found"));
  }

  @Override
  @Transactional
  public OrderStartResponseDto startOrder(User user, OrderStartRequestDto orderRequest) {
    Order order = orderMapper.toEntity(orderRequest);

    order.setDate(LocalDateTime.now());
    order.setStatus(Order.OrderStatus.PENDIENTE);
    order.setAttendedBy(user);

    for (OrderDetail detail : order.getDetails()) {
      Product product = productRepository.findById(detail.getProduct().getId())
          .orElseThrow(() -> new EntityNotFoundException("Product not found"));

      detail.setOrder(order);
      detail.setProduct(product);
      detail.setUnitPrice(product.getPrice());
      detail.setId(OrderDetailId.builder().productId(product.getId()).build());
    }

    return orderMapper.toStartResponse(orderRepository.save(order));
  }

  @Override
  public OrderConfirmSaleResponseDto confirmSale(Long orderId, User cashier, String paymentMethod) {
    Order order = getOrderById(orderId);
    order.setStatus(Order.OrderStatus.PAGADO);

    Sale sale = saleRepository.save(
        Sale.builder()
            .saleDate(LocalDateTime.now())
            .paymentMethod(paymentMethod)
            .totalAmount(order.getDetails().stream()
                .map(detail -> detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .cashier(cashier)
            .order(order)
            .build());

    order.setSale(sale);

    return orderMapper.toConfirmSaleResponse(order);
  }
}