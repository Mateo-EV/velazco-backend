package com.velazco.velazco_backend.services.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.events.PatternEventDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryUpdateResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionFinalizeResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;
import com.velazco.velazco_backend.entities.Sale;
import com.velazco.velazco_backend.entities.Dispatch;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.services.EventPublisherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

  private final RabbitTemplate rabbitTemplate;

  // Product events
  @Override
  public void publishProductCreated(ProductCreateResponseDto product) {
    sendEvent("product.created", product);
  }

  @Override
  public void publishProductUpdated(ProductUpdateResponseDto product) {
    sendEvent("product.updated", product);
  }

  @Override
  public void publishProductActiveUpdated(ProductUpdateActiveResponseDto product) {
    sendEvent("product.updated", product);
  }

  @Override
  public void publishProductDeleted(ProductCreateResponseDto product) {
    sendEvent("product.deleted", product);
  }

  @Override
  public void publishProductStockChanged(Product product, String reason) {
    sendEvent("product.stock.changed", new ProductStockChangedDto(product.getId(), product.getStock(), reason));
  }

  // Category events
  @Override
  public void publishCategoryCreated(CategoryCreateResponseDto category) {
    sendEvent("category.created", category);
  }

  @Override
  public void publishCategoryUpdated(CategoryUpdateResponseDto category) {
    sendEvent("category.updated", category);
  }

  @Override
  public void publishCategoryDeleted(CategoryCreateResponseDto category) {
    sendEvent("category.deleted", category);
  }

  // Order events
  @Override
  public void publishOrderStarted(OrderStartResponseDto order) {
    sendEvent("order.started", order);
  }

  @Override
  public void publishOrderSaleConfirmed(OrderConfirmSaleResponseDto order) {
    sendEvent("order.sale.confirmed", order);
  }

  @Override
  public void publishOrderDispatchConfirmed(OrderConfirmDispatchResponseDto order) {
    sendEvent("order.dispatch.confirmed", order);
  }

  @Override
  public void publishOrderCancelled(OrderStartResponseDto order) {
    sendEvent("order.cancelled", order);
  }

  // Production events
  @Override
  public void publishProductionCreated(ProductionCreateResponseDto production) {
    sendEvent("production.created", production);
  }

  @Override
  public void publishProductionUpdated(ProductionUpdateResponseDto production) {
    sendEvent("production.updated", production);
  }

  @Override
  public void publishProductionFinalized(ProductionFinalizeResponseDto production) {
    sendEvent("production.finalized", production);
  }

  @Override
  public void publishProductionDeleted(ProductionCreateResponseDto production) {
    sendEvent("production.deleted", production);
  }

  // User events
  @Override
  public void publishUserCreated(UserCreateResponseDto user) {
    sendEvent("user.created", user);
  }

  @Override
  public void publishUserUpdated(UserUpdateResponseDto user) {
    sendEvent("user.updated", user);
  }

  @Override
  public void publishUserDeleted(UserCreateResponseDto user) {
    sendEvent("user.deleted", user);
  }

  // Sale events
  @Override
  public void publishSaleCreated(Sale sale) {
    sendEvent("sale.created", sale);
  }

  // Dispatch events
  @Override
  public void publishDispatchCreated(Dispatch dispatch) {
    sendEvent("dispatch.created", dispatch);
  }

  private PatternEventDto<?> createPatternEvent(String pattern, Object data) {
    return new PatternEventDto<>(pattern, data);
  }

  @Async("eventTaskExecutor")
  private void sendEvent(String pattern, Object data) {
    try {
      PatternEventDto<?> event = createPatternEvent(pattern, data);
      rabbitTemplate.convertAndSend("velazco_exchange", pattern, event);
      log.debug("Event sent successfully: pattern={}", pattern);
    } catch (Exception e) {
      log.warn("Failed to send event to RabbitMQ: pattern={}, error={}", pattern, e.getMessage());
    }
  }

  public static class ProductStockChangedDto {
    private Long productId;
    private Integer newStock;
    private String reason;

    public ProductStockChangedDto(Long productId, Integer newStock, String reason) {
      this.productId = productId;
      this.newStock = newStock;
      this.reason = reason;
    }

    // Getters
    public Long getProductId() {
      return productId;
    }

    public Integer getNewStock() {
      return newStock;
    }

    public String getReason() {
      return reason;
    }
  }
}
