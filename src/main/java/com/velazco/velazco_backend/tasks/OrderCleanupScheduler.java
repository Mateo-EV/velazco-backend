package com.velazco.velazco_backend.tasks;

import com.velazco.velazco_backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

  private static final Logger logger = LoggerFactory.getLogger(OrderCleanupScheduler.class);

  private final OrderService orderService;

  @Scheduled(cron = "0 59 23 * * ?")
  public void deleteCancelledOrders() {
    logger.info("🗑️ Ejecutando limpieza de órdenes canceladas (Scheduler iniciado)...");
    orderService.deleteCancelledOrdersOlderThanOneDay();
    logger.info("✅ Limpieza de órdenes canceladas completada.");
  }
}