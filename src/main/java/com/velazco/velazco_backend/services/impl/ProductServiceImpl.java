package com.velazco.velazco_backend.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.exception.FileTooLargeException;
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
  public ProductCreateResponseDto createProductWithImage(ProductCreateRequestDto dto) {
    String imagePath = null;

    if (dto.getImage() != null && !dto.getImage().isEmpty()) {
      if (dto.getImage().getSize() > 4 * 1024 * 1024) {
        throw new FileTooLargeException("La imagen no debe superar los 4 MB.");
      }

      try {
        String filename = UUID.randomUUID() + "_" + dto.getImage().getOriginalFilename();
        Path path = Paths.get("uploads").resolve(filename); // ⬅ Carpeta externa
        Files.createDirectories(path.getParent());
        Files.copy(dto.getImage().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        imagePath = "/storage/" + filename; // ⬅ URL para frontend
      } catch (IOException e) {
        throw new RuntimeException("Error al guardar la imagen", e);
      }
    }

    Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    Product product = productMapper.toEntity(dto);
    product.setImage(imagePath);
    product.setCategory(category);

    Product savedProduct = productRepository.save(product);
    return productMapper.toCreateResponse(savedProduct);
  }

  @Override
  public Product updateProduct(Long id, ProductUpdateRequestDto dto) {
    Product existing = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    String imagePath = existing.getImage();

    if (dto.getImage() != null && !dto.getImage().isEmpty()) {
      if (dto.getImage().getSize() > 4 * 1024 * 1024) {
        throw new FileTooLargeException("La imagen no debe superar los 4 MB.");
      }

      try {
        String filename = UUID.randomUUID() + "_" + dto.getImage().getOriginalFilename();
        Path path = Paths.get("uploads").resolve(filename); // ⬅ Carpeta externa
        Files.createDirectories(path.getParent());
        Files.copy(dto.getImage().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        imagePath = "/storage/" + filename; // ⬅ URL para frontend
      } catch (IOException e) {
        throw new RuntimeException("Error al guardar la imagen", e);
      }
    }

    Product updated = productMapper.toEntity(dto);
    updated.setId(existing.getId());
    updated.setCategory(category);
    updated.setImage(imagePath);
    updated.setOrderDetails(existing.getOrderDetails());
    updated.setProductionDetails(existing.getProductionDetails());

    return productRepository.save(updated);
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
