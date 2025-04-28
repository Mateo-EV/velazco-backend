package com.velazco.velazco_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velazco.velazco_backend.dto.product.requests.ProductUpdateActiveRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.mappers.ProductMapper;
import com.velazco.velazco_backend.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;
  private final ProductMapper productMapper;

  public ProductController(
      ProductService productService,
      ProductMapper productMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
  }

  @PatchMapping("/{id}/active")
  public ResponseEntity<ProductUpdateActiveResponseDto> updateProductActive(
      @PathVariable Long id,
      @Valid @RequestBody ProductUpdateActiveRequestDto statusDTO) {
    Product updatedProduct = productService.updateProductActive(id, statusDTO.getActive());

    return ResponseEntity.ok(productMapper.toUpdateActiveResponse(updatedProduct));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProductById(id);
    return ResponseEntity.noContent().build();
  }
}