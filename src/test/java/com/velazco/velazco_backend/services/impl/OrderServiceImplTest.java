package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.OrderDetail;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.repositories.OrderRepository;
import com.velazco.velazco_backend.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private OrderServiceImpl orderService;

  private Order cancelledOrder;
  private OrderDetail detail;
  private Product product;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setId(1L);
    product.setStock(10);

    detail = new OrderDetail();
    detail.setProduct(product);
    detail.setQuantity(5);

    cancelledOrder = new Order();
    cancelledOrder.setId(100L);
    cancelledOrder.setStatus(Order.OrderStatus.CANCELADO);
    cancelledOrder.setDetails(Collections.singletonList(detail));
  }

  @Test
  void cancelOrder_ShouldRestoreStockAndSetStatusToCancelled() {

    Order pendingOrder = new Order();
    pendingOrder.setId(100L);
    pendingOrder.setStatus(Order.OrderStatus.PENDIENTE);
    pendingOrder.setDetails(Collections.singletonList(detail));

    when(orderRepository.findById(100L)).thenReturn(java.util.Optional.of(pendingOrder));
    when(productRepository.restoreStock(product.getId(), detail.getQuantity())).thenReturn(1);

    orderService.cancelOrder(100L);

    verify(orderRepository, times(1)).findById(100L);

    verify(productRepository, times(1)).restoreStock(product.getId(), detail.getQuantity());

    assert pendingOrder.getStatus() == Order.OrderStatus.CANCELADO;

    verify(orderRepository, times(1)).save(pendingOrder);
  }

}
