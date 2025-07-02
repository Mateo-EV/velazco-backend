package com.velazco.velazco_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionFinalizeRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionStatusUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionDailyResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionFinalizeResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionPendingResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionStatusUpdateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.services.ProductionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
public class ProductionController {
  private final ProductionService productionService;

  @GetMapping("/pending")
  public ResponseEntity<List<ProductionPendingResponseDto>> getPendingProductions() {
    List<ProductionPendingResponseDto> response = productionService.getPendingProductions();
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ProductionCreateResponseDto> createProduction(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody ProductionCreateRequestDto request) {
    ProductionCreateResponseDto response = productionService.createProduction(request, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteProduction(@PathVariable Long id) {
    productionService.deleteProductionById(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductionUpdateResponseDto> updateProduction(
      @PathVariable Long id,
      @AuthenticationPrincipal User user,
      @RequestBody @Valid ProductionUpdateRequestDto request) {
    ProductionUpdateResponseDto response = productionService.updateProduction(id, request, user);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/daily")
  public List<ProductionDailyResponseDto> getDailyProductions() {
    return productionService.getDailyProductions();
  }

  @GetMapping("/historial")
  public ResponseEntity<List<ProductionHistoryResponseDto>> getHistorialCompletosEIncompletos() {
    return ResponseEntity.ok(productionService.getCompletedAndIncompleteOrders());
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ProductionStatusUpdateResponseDto> actualizarEstadoProduccion(
      @PathVariable Long id,
      @RequestBody ProductionStatusUpdateRequestDto dto) {
    ProductionStatusUpdateResponseDto response = productionService.cambiarEstadoPendienteAEnProceso(id, dto);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/finalizar")
  public ResponseEntity<ProductionFinalizeResponseDto> finalizarProduccion(
      @PathVariable Long id,
      @RequestBody @Valid ProductionFinalizeRequestDto request) {
    ProductionFinalizeResponseDto response = productionService.finalizarProduccion(id, request);
    return ResponseEntity.ok(response);
  }

}