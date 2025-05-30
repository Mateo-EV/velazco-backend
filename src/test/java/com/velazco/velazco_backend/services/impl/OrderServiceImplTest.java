package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void deleteCancelledOrdersOlderThanOneDay_ShouldCallRepository() {

        orderService.deleteCancelledOrdersOlderThanOneDay();

        verify(orderRepository, times(1))
                .deleteByStatusAndDateBefore(
                        eq(Order.OrderStatus.CANCELADO),
                        any(LocalDateTime.class));
    }
}
