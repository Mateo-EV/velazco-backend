package com.velazco.velazco_backend.services.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.events.PatternEventDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.services.EventPublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventPublisherServiceImpl implements EventPublisherService {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publishProductCreated(ProductCreateResponseDto product) {
    sendEvent("product.created", product);
  }

  private PatternEventDto<?> createPatternEvent(String pattern, Object data) {
    return new PatternEventDto<>(pattern, data);
  }

  private void sendEvent(String pattern, Object data) {
    PatternEventDto<?> event = createPatternEvent(pattern, data);
    rabbitTemplate.convertAndSend("velazco_exchange", pattern, event);
  }
}
