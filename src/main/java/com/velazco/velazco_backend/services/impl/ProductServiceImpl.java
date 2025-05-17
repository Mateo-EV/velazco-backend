package com.velazco.velazco_backend.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
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
  public List<ProductListResponseDto> getAllProducts() {
    List<Product> products = productRepository.findAll();
    return productMapper.toListResponse(products);
  }

  @Override
  public List<ProductListResponseDto> getAllAvailableProducts() {
    return productMapper.toListResponse(productRepository.findAvailableProducts());
  }

  @Override
  public ProductCreateResponseDto createProduct(ProductCreateRequestDto dto) {
    Product product = productMapper.toEntity(dto);

    Category category = categoryRepository.findById(product.getCategory().getId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    product.setCategory(category);
    Product savedProduct = productRepository.save(product);

    return productMapper.toCreateResponse(savedProduct);
  }

  @Override
  public ProductUpdateResponseDto updateProduct(Long id, ProductUpdateRequestDto dto) {
    Product product = productMapper.toEntity(dto);

    Product existing = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    product.setId(existing.getId());

    Category category = categoryRepository.findById(product.getCategory().getId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    product.setCategory(category);

    Product updated = productRepository.save(product);

    return productMapper.toUpdateResponse(updated);
  }

  @Override
  public void deleteProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    productRepository.delete(product);
  }

  @Override
  public ProductUpdateActiveResponseDto updateProductActive(Long id, Boolean active) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    product.setActive(active);
    Product updated = productRepository.save(product);

    return productMapper.toUpdateActiveResponse(updated);
  }

}
