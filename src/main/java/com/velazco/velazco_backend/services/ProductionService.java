package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionListResponseDto;
import com.velazco.velazco_backend.entities.User;

public interface ProductionService {
  List<ProductionCreateResponseDto> getAllProductions();

  ProductionCreateResponseDto createProduction(ProductionCreateRequestDto request, User assignedBy);

  void deleteProductionById(Long productionId);

  ProductionCreateResponseDto updateProduction(Long productionId, ProductionUpdateRequestDto request,
      User updatedBy);

  List<ProductionListResponseDto> getDailyProductions();
}