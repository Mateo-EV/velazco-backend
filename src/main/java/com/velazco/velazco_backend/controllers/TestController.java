package com.velazco.velazco_backend.controllers;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

  private final Clock clock;

  @GetMapping("/")
  public String hello() {
    return "Hello world from Java Spring Boot!";
  }

  @GetMapping("/clock")
  public String getDateNow() {
    LocalDate today = LocalDate.now(clock);
    return "Hoy en Perú: " + today;
  }
}
