package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.SaleRepository;
import com.velazco.velazco_backend.services.SaleService;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl implements SaleService {

  private final SaleRepository saleRepository;

  public SaleServiceImpl(SaleRepository saleRepository) {
    this.saleRepository = saleRepository;
  }
}