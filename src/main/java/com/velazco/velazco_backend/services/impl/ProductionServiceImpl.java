package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.ProductionRepository;
import com.velazco.velazco_backend.services.ProductionService;
import org.springframework.stereotype.Service;

@Service
public class ProductionServiceImpl implements ProductionService {
  @SuppressWarnings("unused")
  private final ProductionRepository productionRepository;

  public ProductionServiceImpl(ProductionRepository productionRepository) {
    this.productionRepository = productionRepository;
  }
}