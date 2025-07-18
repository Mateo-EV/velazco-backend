package com.velazco.velazco_backend.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.DailySaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.DeliveredOrderResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.dto.order.responses.PaymentMethodSummaryDto;
import com.velazco.velazco_backend.dto.order.responses.TopProductDto;
import com.velazco.velazco_backend.dto.order.responses.WeeklySaleResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.User;

public interface OrderService {
  PaginatedResponseDto<OrderListResponseDto> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);

  Order getOrderById(Long id);

  OrderStartResponseDto startOrder(User user, OrderStartRequestDto orderRequest);

  OrderConfirmSaleResponseDto confirmSale(Long orderId, User cashier, String paymentMethod);

  void deleteCancelledOrdersOlderThanOneDay();

  OrderConfirmDispatchResponseDto confirmDispatch(Long orderId, User dispatchedBy);

  void cancelOrder(Long orderId);

  PaginatedResponseDto<OrderListResponseDto> filterOrders(String status,
      Long orderId,
      String clientName,
      Pageable pageable);

  PaginatedResponseDto<DeliveredOrderResponseDto> getDeliveredOrders(Pageable pageable);

  List<DailySaleResponseDto> getDailySalesDetailed();

  List<WeeklySaleResponseDto> getWeeklySalesDetailed();

  List<TopProductDto> getTopSellingProductsOfCurrentMonth();

  List<PaymentMethodSummaryDto> getSalesByPaymentMethod();

}
