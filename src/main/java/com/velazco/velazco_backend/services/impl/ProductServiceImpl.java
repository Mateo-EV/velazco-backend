package com.velazco.velazco_backend.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.services.ProductService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductServiceImpl(ProductRepository productRepository,
      CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
  }

  @Override
  public List<Product> getAllProducts() {
      return productRepository.findAll();
  }

  @Override
  public Product createProduct(Product product) {
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
