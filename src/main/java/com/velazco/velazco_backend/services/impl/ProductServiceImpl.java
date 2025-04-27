package com.velazco.velazco_backend.services.impl;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.services.ProductService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  public ProductServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public void deleteProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    productRepository.delete(product);
  }
}
