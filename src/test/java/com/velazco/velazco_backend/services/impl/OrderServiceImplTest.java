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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void deleteCancelledOrdersOlderThanOneDay_ShouldCallRepositories() {

        List<Order> cancelledOrders = Collections.singletonList(cancelledOrder);

        when(orderRepository.findByStatusAndDateBefore(eq(Order.OrderStatus.CANCELADO), any(LocalDateTime.class)))
                .thenReturn(cancelledOrders);

        lenient().when(productRepository.findById(product.getId()))
                .thenReturn(java.util.Optional.of(product));

        when(productRepository.restoreStock(product.getId(), detail.getQuantity()))
                .thenReturn(1);

        orderService.deleteCancelledOrdersOlderThanOneDay();

        verify(orderRepository, times(1))
                .findByStatusAndDateBefore(eq(Order.OrderStatus.CANCELADO), any(LocalDateTime.class));

        verify(productRepository, times(1))
                .restoreStock(eq(product.getId()), eq(detail.getQuantity()));

        verify(orderRepository, times(1)).deleteAll(cancelledOrders);
    }

}
