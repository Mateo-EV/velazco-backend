package com.velazco.velazco_backend.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.velazco.velazco_backend.dto.order.responses.PaymentMethodSummaryDto;
import com.velazco.velazco_backend.dto.order.responses.TopProductDto;
import com.velazco.velazco_backend.dto.order.responses.WeeklySaleResponseDto;
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
import com.velazco.velazco_backend.services.EventPublisherService;
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
  private final EventPublisherService eventPublisherService;

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
      product.setStock(product.getStock() - quantity);

      eventPublisherService.publishProductStockChanged(product, "ORDER_STARTED");

      detail.setOrder(order);
      detail.setProduct(product);
      detail.setUnitPrice(product.getPrice());
      detail.setId(OrderDetailId.builder().productId(product.getId()).build());
    }

    Order savedOrder = orderRepository.save(order);
    OrderStartResponseDto response = orderMapper.toStartResponse(savedOrder);

    eventPublisherService.publishOrderStarted(response);

    return response;
  }

  @Transactional
  @Override
  public OrderConfirmSaleResponseDto confirmSale(Long orderId, User cashier, String paymentMethod) {
    Order order = getOrderById(orderId);

    if (order.getStatus() != Order.OrderStatus.PENDIENTE) {
      throw new IllegalStateException(
          "No se puede confirmar la venta porque la orden está en estado: " + order.getStatus());
    }

    String metodo = paymentMethod.toUpperCase();

    if (!Set.of("EFECTIVO", "TARJETA", "TRANSFERENCIA").contains(metodo)) {
      throw new IllegalArgumentException(
          "Método de pago inválido. Solo se permiten: EFECTIVO, TARJETA o TRANSFERENCIA.");
    }

    order.setStatus(Order.OrderStatus.PAGADO);

    Sale sale = saleRepository.save(
        Sale.builder()
            .saleDate(LocalDateTime.now())
            .paymentMethod(metodo) // se guarda en mayúsculas
            .totalAmount(order.getDetails().stream()
                .map(detail -> detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .cashier(cashier)
            .order(order)
            .build());
    order.setSale(sale);
    orderRepository.save(order);

    OrderConfirmSaleResponseDto response = orderMapper.toConfirmSaleResponse(order);

    eventPublisherService.publishSaleCreated(sale);
    eventPublisherService.publishOrderSaleConfirmed(response);

    return response;
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

    OrderConfirmDispatchResponseDto response = orderMapper.toConfirmDispatchResponse(order);

    eventPublisherService.publishDispatchCreated(dispatch);
    eventPublisherService.publishOrderDispatchConfirmed(response);

    return response;
  }

  @Transactional
  @Override
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

    if (order.getStatus() != Order.OrderStatus.PENDIENTE) {
      throw new IllegalStateException("El pedido no puede ser cancelado porque ya está " + order.getStatus());
    } // Restaurar stock de cada producto del pedido
    for (OrderDetail detail : order.getDetails()) {
      Long productId = detail.getProduct().getId();
      int quantity = detail.getQuantity();

      int updatedRows = productRepository.restoreStock(productId, quantity);
      if (updatedRows == 0) {
        throw new EntityNotFoundException("Product not found with ID: " + productId);
      }

      // Publish stock change event
      Product updatedProduct = productRepository.findById(productId)
          .orElseThrow(() -> new EntityNotFoundException("Product not found"));
      eventPublisherService.publishProductStockChanged(updatedProduct, "ORDER_CANCELLED");
    }

    // Cambiar estado del pedido a CANCELADO
    order.setStatus(Order.OrderStatus.CANCELADO);
    orderRepository.save(order);

    eventPublisherService.publishOrderCancelled(orderMapper.toStartResponse(order));
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

    Map<LocalDate, List<DailySaleResponseDto.ProductSold>> groupedProductsByDate = rawResults.stream()
        .collect(Collectors.groupingBy(
            row -> ((java.sql.Date) row[0]).toLocalDate(),
            Collectors.mapping(row -> DailySaleResponseDto.ProductSold.builder()
                .productName((String) row[1])
                .quantitySold((Integer) row[2])
                .unitPrice((BigDecimal) row[3])
                .subtotal((BigDecimal) row[4])
                .build(),
                Collectors.toList())));
    Map<LocalDate, Long> salesCountByDate = rawResults.stream()
        .collect(Collectors.groupingBy(
            row -> ((java.sql.Date) row[0]).toLocalDate(),
            Collectors.mapping(row -> (Long) row[5], Collectors.toSet())))
        .entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> (long) entry.getValue().size()));

    // Construir lista de DTOs
    return groupedProductsByDate.entrySet().stream()
        .map(entry -> {
          LocalDate date = entry.getKey();
          List<DailySaleResponseDto.ProductSold> products = entry.getValue();

          BigDecimal total = products.stream()
              .map(DailySaleResponseDto.ProductSold::getSubtotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

          return DailySaleResponseDto.builder()
              .date(date)
              .totalSales(total)
              .salesCount(salesCountByDate.getOrDefault(date, 0L).intValue())
              .products(products)
              .build();
        })
        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
        .toList();
  }

  @Override
  public List<WeeklySaleResponseDto> getWeeklySalesDetailed() {
    List<Object[]> rows = orderRepository.findDeliveredOrderDetailsForWeek();

    Map<LocalDate, List<Object[]>> groupedByWeekStart = rows.stream()
        .collect(Collectors.groupingBy(row -> {
          LocalDate deliveryDate = ((java.sql.Date) row[0]).toLocalDate();
          return deliveryDate.minusDays(deliveryDate.getDayOfWeek().getValue() % 7);
        }));

    return groupedByWeekStart.entrySet().stream()
        .map(weekEntry -> {
          LocalDate start = weekEntry.getKey();
          LocalDate end = start.plusDays(6);

          Map<Long, List<Object[]>> ordersGrouped = weekEntry.getValue().stream()
              .collect(Collectors.groupingBy(row -> ((Number) row[1]).longValue()));

          List<WeeklySaleResponseDto.DeliveredOrder> deliveredOrders = ordersGrouped.entrySet().stream()
              .map(orderEntry -> {
                Long orderId = orderEntry.getKey();
                List<Object[]> orderRows = orderEntry.getValue();

                LocalDate deliveryDate = ((java.sql.Date) orderRows.get(0)[0]).toLocalDate();
                String dayOfWeek = deliveryDate.getDayOfWeek().toString();

                List<WeeklySaleResponseDto.ProductSold> products = orderRows.stream()
                    .map(row -> WeeklySaleResponseDto.ProductSold.builder()
                        .productName((String) row[2])
                        .quantitySold((Integer) row[3])
                        .unitPrice((BigDecimal) row[4])
                        .subtotal((BigDecimal) row[5])
                        .build())
                    .toList();

                BigDecimal orderTotal = products.stream()
                    .map(WeeklySaleResponseDto.ProductSold::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                return WeeklySaleResponseDto.DeliveredOrder.builder()
                    .orderId(orderId)
                    .deliveryDate(deliveryDate)
                    .dayOfWeek(dayOfWeek)
                    .orderTotal(orderTotal)
                    .products(products)
                    .build();
              })
              .toList();

          BigDecimal totalSales = deliveredOrders.stream()
              .map(WeeklySaleResponseDto.DeliveredOrder::getOrderTotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

          return WeeklySaleResponseDto.builder()
              .startDate(start)
              .endDate(end)
              .totalSales(totalSales)
              .salesCount(deliveredOrders.size())
              .orders(deliveredOrders)
              .build();
        })
        .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
        .toList();
  }

  @Override
  public List<TopProductDto> getTopSellingProductsOfCurrentMonth() {
    LocalDate now = LocalDate.now();
    LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
    LocalDateTime endOfMonth = now.plusDays(1).atStartOfDay(); // para incluir el día de hoy

    List<Object[]> results = orderRepository.findTopSellingProductsOfMonth(startOfMonth, endOfMonth);

    return results.stream().map(row -> TopProductDto.builder()
        .productName((String) row[0])
        .totalQuantitySold(((Number) row[1]).intValue())
        .totalRevenue((BigDecimal) row[2])
        .build()).toList();
  }

  @Override
  public List<PaymentMethodSummaryDto> getSalesByPaymentMethod() {
    List<Sale> allSales = saleRepository.findAll(); // o usar un query con status ENTREGADO si es necesario

    BigDecimal totalGlobal = allSales.stream()
        .map(Sale::getTotalAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    Map<String, BigDecimal> totalByMethod = allSales.stream()
        .collect(Collectors.groupingBy(
            sale -> sale.getPaymentMethod().toUpperCase(),
            Collectors.mapping(Sale::getTotalAmount,
                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

    return totalByMethod.entrySet().stream()
        .map(entry -> {
          BigDecimal totalSales = entry.getValue();
          double percentage = totalGlobal.compareTo(BigDecimal.ZERO) == 0
              ? 0.0
              : totalSales.multiply(BigDecimal.valueOf(100))
                  .divide(totalGlobal, 2, RoundingMode.HALF_UP)
                  .doubleValue();

          return PaymentMethodSummaryDto.builder()
              .paymentMethod(entry.getKey())
              .totalSales(totalSales)
              .percentage(percentage)
              .build();
        })
        .toList();
  }

}
