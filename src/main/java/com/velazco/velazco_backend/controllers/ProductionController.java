package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.services.ProductionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
public class ProductionController {
  private final ProductionService productionService;

  @PostMapping
  ResponseEntity<ProductionCreateResponseDto> createProduction(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid ProductionCreateRequestDto request) {
    ProductionCreateResponseDto response = productionService.createProduction(request, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteProduction(@PathVariable Long id) {
    productionService.deleteProductionById(id);
    return ResponseEntity.noContent().build();
  }
}