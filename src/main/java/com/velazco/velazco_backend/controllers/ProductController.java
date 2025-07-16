package com.velazco.velazco_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.velazco.velazco_backend.dto.product.responses.ProductLowStockResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
import com.velazco.velazco_backend.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(
      ProductService productService) {
    this.productService = productService;
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping
  public ResponseEntity<List<ProductListResponseDto>> getAllProducts() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  @PreAuthorize("hasAnyRole('Administrador','Vendedor')")
  @GetMapping("/available")
  public ResponseEntity<List<ProductListResponseDto>> getAllAvailableProducts() {
    List<ProductListResponseDto> products = productService.getAllAvailableProducts();
    return ResponseEntity.ok(products);
  }

  @PreAuthorize("hasRole('Administrador')")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductCreateResponseDto> createProduct(
      @Valid @ModelAttribute ProductCreateRequestDto requestDTO) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(productService.createProduct(requestDTO));
  }

  @PreAuthorize("hasRole('Administrador')")
  @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductUpdateResponseDto> updateProduct(
      @PathVariable Long id,
      @Valid @ModelAttribute ProductUpdateRequestDto requestDTO) {

    ProductUpdateResponseDto response = productService.updateProduct(id, requestDTO);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('Administrador')")
  @PatchMapping("/{id}/active")
  public ResponseEntity<ProductUpdateActiveResponseDto> updateProductActive(
      @PathVariable Long id,
      @Valid @RequestBody ProductUpdateActiveRequestDto statusDTO) {

    ProductUpdateActiveResponseDto responseDTO = productService.updateProductActive(id, statusDTO.getActive());
    return ResponseEntity.ok(responseDTO);
  }

  @PreAuthorize("hasRole('Administrador')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProductById(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping("/low-stock")
  public ResponseEntity<ProductLowStockResponseDto> getLowStockProducts() {
    return ResponseEntity.ok(productService.getLowStockProducts());
  }

}