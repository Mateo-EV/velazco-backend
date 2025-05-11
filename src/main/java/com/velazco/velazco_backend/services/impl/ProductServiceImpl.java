package com.velazco.velazco_backend.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.mappers.ProductMapper;
import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.services.ProductService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public List<ProductListResponseDto> getAllAvailableProducts() {
    return productMapper.toListResponse(productRepository.findAvailableProducts());
  }

  @Override
  public Product createProduct(Product product) {
    Category category = categoryRepository.findById(product.getCategory().getId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    product.setCategory(category);

    return productRepository.save(product);
  }

  @Override
  public Product updateProduct(Long id, Product product) {
    Product existing = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    product.setId(existing.getId());

    Category category = categoryRepository.findById(product.getCategory().getId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    product.setCategory(category);

    return productRepository.save(product);
  }

  @Override
  public void deleteProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    productRepository.delete(product);
  }

  @Override
  public Product updateProductActive(Long id, Boolean active) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    product.setActive(active);

    return productRepository.save(product);
  }
}
