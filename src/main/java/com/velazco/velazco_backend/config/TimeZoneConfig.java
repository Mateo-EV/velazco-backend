package com.velazco.velazco_backend.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.ClockProvider;

@Configuration
public class TimeZoneConfig {

  @Bean
  Clock systemClock() {
    return Clock.system(ZoneId.of("America/Lima"));
  }

  @Bean
  ClockProvider clockProvider(Clock clock) {
    return () -> clock;
  }
}
