package com.velazco.velazco_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateActiveRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
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

  @GetMapping
  public ResponseEntity<List<ProductListResponseDto>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    return ResponseEntity.ok(productMapper.toListResponse(products));
  }

  @GetMapping("/available")
  public ResponseEntity<List<ProductListResponseDto>> getAllAvailableProducts() {
    List<ProductListResponseDto> products = productService.getAllAvailableProducts();
    return ResponseEntity.ok(products);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductCreateResponseDto> createProduct(
      @Valid @ModelAttribute ProductCreateRequestDto requestDTO) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(productService.createProductWithImage(requestDTO));
  }

  @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductUpdateResponseDto> updateProduct(
      @PathVariable Long id,
      @Valid @ModelAttribute ProductUpdateRequestDto requestDTO) {

    Product updatedProduct = productService.updateProduct(id, requestDTO);
    return ResponseEntity.ok(productMapper.toUpdateResponse(updatedProduct));
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