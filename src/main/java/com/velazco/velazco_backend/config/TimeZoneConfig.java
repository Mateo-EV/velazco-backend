package com.velazco.velazco_backend.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

  @Bean
  Clock systemClock() {
    return Clock.system(ZoneId.of("America/Lima"));
  }
}
