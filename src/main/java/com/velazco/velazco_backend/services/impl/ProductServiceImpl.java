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
import com.velazco.velazco_backend.dto.product.responses.ProductLowStockResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.exception.FileTooLargeException;
import com.velazco.velazco_backend.exception.GeneralBadRequestException;
import com.velazco.velazco_backend.mappers.ProductMapper;
import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.services.EventPublisherService;
import com.velazco.velazco_backend.services.ImageStorageService;
import com.velazco.velazco_backend.services.ProductService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;
  private final ImageStorageService imageStorageService;
  private final EventPublisherService eventPublisherService;

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
        imagePath = "/storage/" + filename;
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
    ProductCreateResponseDto productMapped = productMapper.toCreateResponse(savedProduct);

    eventPublisherService.publishProductCreated(productMapped);

    return productMapped;
  }

  @Override
  public ProductUpdateResponseDto updateProduct(Long id, ProductUpdateRequestDto dto) {
    Product existing = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    String imagePath = existing.getImage();

    if (dto.getImage() != null && !dto.getImage().isEmpty()) {
      imageStorageService.validateSize(dto.getImage());
      imagePath = imageStorageService.store(dto.getImage());
      imageStorageService.delete(existing.getImage());
    }

    Product updated = productMapper.toEntity(dto);
    updated.setId(existing.getId());
    updated.setCategory(category);
    updated.setImage(imagePath);
    updated.setOrderDetails(existing.getOrderDetails());
    updated.setProductionDetails(existing.getProductionDetails());
    Product savedProduct = productRepository.save(updated);
    ProductUpdateResponseDto response = productMapper.toUpdateResponse(savedProduct);

    eventPublisherService.publishProductUpdated(response);

    return response;
  }

  @Override
  @Transactional
  public void deleteProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    if (productRepository.hasOrderDetails(id) || productRepository.hasProductionDetails(id)) {
      throw new GeneralBadRequestException(
          "No se puede eliminar un producto que tiene órdenes o detalles de producción asociados");
    }

    // Crear el DTO de respuesta antes de eliminar
    ProductCreateResponseDto deletedProduct = productMapper.toCreateResponse(product);

    productRepository.delete(product);
    imageStorageService.delete(product.getImage());

    eventPublisherService.publishProductDeleted(deletedProduct);
  }

  @Override
  public ProductUpdateActiveResponseDto updateProductActive(Long id, Boolean active) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    product.setActive(active);
    Product updated = productRepository.save(product);

    ProductUpdateActiveResponseDto response = productMapper.toUpdateActiveResponse(updated);
    eventPublisherService.publishProductActiveUpdated(response);

    return response;
  }

  @Override
  public ProductLowStockResponseDto getLowStockProducts() {
    List<Product> products = productRepository.findLowStockProducts();
    List<ProductLowStockResponseDto.ProductData> productData = productMapper.toLowStockProductData(products);
    return new ProductLowStockResponseDto((long) productData.size(), productData);
  }

}
