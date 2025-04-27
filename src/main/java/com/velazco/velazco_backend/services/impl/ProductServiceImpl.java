package com.velazco.velazco_backend.services.impl;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.services.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  public ProductServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

}
