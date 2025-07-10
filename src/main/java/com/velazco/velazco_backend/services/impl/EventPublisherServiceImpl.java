package com.velazco.velazco_backend.services.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.events.PatternEventDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.services.EventPublisherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publishProductCreated(ProductCreateResponseDto product) {
    sendEvent("product.created", product);
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
}
