package com.velazco.velazco_backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMqConfig {
  public static final String EXCHANGE_NAME = "velazco_exchange";
  public static final String QUEUE_NAME = "velazco_realtime_queue";

  @Bean
  TopicExchange notificationsExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  Queue notificationsQueue() {
    return new Queue(QUEUE_NAME, true);
  }

  @Bean
  Binding productoBinding() {
    return BindingBuilder
        .bind(notificationsQueue())
        .to(notificationsExchange())
        .with("product.*");
  }

  @Bean
  MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter());
    return template;
  }
}
