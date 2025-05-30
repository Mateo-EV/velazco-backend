package com.velazco.velazco_backend.tasks;

import com.velazco.velazco_backend.services.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private final OrderService orderService;

    @Scheduled(cron = "0 59 23 * * ?")
    public void deleteCancelledOrders() {
        orderService.deleteCancelledOrdersOlderThanOneDay();
    }
}