package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.services.ProductionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productions")
public class ProductionController {
  @SuppressWarnings("unused")
  private final ProductionService productionService;

  public ProductionController(ProductionService productionService) {
    this.productionService = productionService;
  }
}