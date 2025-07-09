package com.velazco.velazco_backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.DailySaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.DeliveredOrderResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Dispatch;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.OrderDetail;
import com.velazco.velazco_backend.entities.OrderDetailId;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Sale;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.entities.Order.OrderStatus;
import com.velazco.velazco_backend.mappers.OrderMapper;
import com.velazco.velazco_backend.repositories.DispatchRepository;
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
  private final DispatchRepository dispatchRepository;

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

    List<Long> productIds = order.getDetails().stream()
        .map(detail -> detail.getProduct().getId())
        .distinct()
        .toList();

    List<Product> products = productRepository.findAllById(productIds);
    Map<Long, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getId, p -> p));

    for (OrderDetail detail : order.getDetails()) {
      Long productId = detail.getProduct().getId();
      int quantity = detail.getQuantity();

      int updatedRows = productRepository.decrementStock(productId, quantity);
      if (updatedRows == 0) {
        throw new IllegalStateException("No hay suficiente stock para el producto con ID: " + productId);
      }

      Product product = productMap.get(productId);
      if (product == null) {
        throw new EntityNotFoundException("Product not found with ID: " + productId);
      }

      detail.setOrder(order);
      detail.setProduct(product);
      detail.setUnitPrice(product.getPrice());
      detail.setId(OrderDetailId.builder().productId(product.getId()).build());
    }

    Order savedOrder = orderRepository.save(order);
    return orderMapper.toStartResponse(savedOrder);
  }

  @Transactional
  @Override
  public OrderConfirmSaleResponseDto confirmSale(Long orderId, User cashier, String paymentMethod) {
    Order order = getOrderById(orderId);

    if (order.getStatus() != Order.OrderStatus.PENDIENTE) {
      throw new IllegalStateException(
          "No se puede confirmar la venta porque la orden está en estado: " + order.getStatus());
    }

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

    orderRepository.save(order);

    return orderMapper.toConfirmSaleResponse(order);
  }

  @Override
  @Transactional
  public void deleteCancelledOrdersOlderThanOneDay() {
    LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1);
    List<Order> cancelledOrders = orderRepository.findByStatusAndDateBefore(Order.OrderStatus.CANCELADO, cutoffTime);

    orderRepository.deleteAll(cancelledOrders);
  }

  @Override
  @Transactional
  public OrderConfirmDispatchResponseDto confirmDispatch(Long orderId, User dispatchedBy) {
    Order order = getOrderById(orderId);

    if (order.getStatus() != Order.OrderStatus.PAGADO) {
      throw new IllegalStateException("El pedido no puede ser enviado porque está en estado: " + order.getStatus());
    }

    order.setStatus(OrderStatus.ENTREGADO);

    Dispatch dispatch = dispatchRepository.save(
        Dispatch.builder()
            .deliveryDate(LocalDateTime.now())
            .order(order)
            .dispatchedBy(dispatchedBy)
            .build());

    order.setDispatch(dispatch);

    orderRepository.save(order);

    return orderMapper.toConfirmDispatchResponse(order);
  }

  @Transactional
  @Override
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

    if (order.getStatus() != Order.OrderStatus.PENDIENTE) {
      throw new IllegalStateException("El pedido no puede ser cancelado porque ya está " + order.getStatus());
    }

    // Restaurar stock de cada producto del pedido
    for (OrderDetail detail : order.getDetails()) {
      Long productId = detail.getProduct().getId();
      int quantity = detail.getQuantity();

      int updatedRows = productRepository.restoreStock(productId, quantity);
      if (updatedRows == 0) {
        throw new EntityNotFoundException("Product not found with ID: " + productId);
      }
    }

    // Cambiar estado del pedido a CANCELADO
    order.setStatus(Order.OrderStatus.CANCELADO);
    orderRepository.save(order);
  }

  @Override
  public PaginatedResponseDto<OrderListResponseDto> filterOrders(
      String status,
      Long orderId,
      String clientName,
      Pageable pageable) {

    Order.OrderStatus orderStatus;
    try {
      orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Estado inválido. Solo se permite PAGADO o ENTREGADO.");
    }

    if (orderStatus != Order.OrderStatus.PAGADO && orderStatus != Order.OrderStatus.ENTREGADO) {
      throw new IllegalArgumentException("Estado inválido. Solo se permite PAGADO o ENTREGADO.");
    }

    Page<Order> orderPage = orderRepository.findByStatusAndOptionalFilters(orderStatus, orderId, clientName, pageable);

    return PaginatedResponseDto.<OrderListResponseDto>builder()
        .content(orderMapper.toListResponse(orderPage.getContent()))
        .currentPage(orderPage.getNumber())
        .totalItems(orderPage.getTotalElements())
        .totalPages(orderPage.getTotalPages())
        .build();
  }

  @Override
  public PaginatedResponseDto<DeliveredOrderResponseDto> getDeliveredOrders(Pageable pageable) {
    Page<Order> orderPage = orderRepository.findByStatus(Order.OrderStatus.ENTREGADO, pageable);

    List<DeliveredOrderResponseDto> dtoList = orderPage.getContent().stream()
        .map(orderMapper::toDeliveredDto)
        .toList();

    return PaginatedResponseDto.<DeliveredOrderResponseDto>builder()
        .content(dtoList)
        .currentPage(orderPage.getNumber())
        .totalItems(orderPage.getTotalElements())
        .totalPages(orderPage.getTotalPages())
        .build();
  }

  @Override
  public List<DailySaleResponseDto> getDailySalesDetailed() {
    List<Object[]> rawResults = orderRepository.findDetailedDeliveredSales();

    Map<LocalDate, List<DailySaleResponseDto.ProductSold>> groupedByDate = rawResults.stream()
        .collect(Collectors.groupingBy(
            row -> ((java.sql.Date) row[0]).toLocalDate(),
            Collectors.mapping(row -> DailySaleResponseDto.ProductSold.builder()
                .productName((String) row[1])
                .quantitySold((Integer) row[2])
                .unitPrice((BigDecimal) row[3])
                .subtotal((BigDecimal) row[4])
                .build(),
                Collectors.toList())));

    return groupedByDate.entrySet().stream()
        .map(entry -> {
          BigDecimal total = entry.getValue().stream()
              .map(DailySaleResponseDto.ProductSold::getSubtotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

          return DailySaleResponseDto.builder()
              .date(entry.getKey())
              .totalSales(total)
              .products(entry.getValue())
              .build();
        })
        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
        .toList();
  }

}
