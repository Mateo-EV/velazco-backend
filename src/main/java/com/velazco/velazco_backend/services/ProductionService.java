package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.entities.User;

public interface ProductionService {
  ProductionCreateResponseDto createProduction(ProductionCreateRequestDto request, User assignedBy);

  void deleteProduction(Long productionId);
}