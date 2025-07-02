package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionFinalizeRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionStatusUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionDailyResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionFinalizeResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionPendingResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionProcessResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionStatusUpdateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.entities.User;

public interface ProductionService {
  List<ProductionPendingResponseDto> getPendingProductions();

  List<ProductionProcessResponseDto> getProductionsInProcess();

  ProductionCreateResponseDto createProduction(ProductionCreateRequestDto request, User assignedBy);

  void deleteProductionById(Long productionId);

  ProductionUpdateResponseDto updateProduction(Long productionId, ProductionUpdateRequestDto request,
      User updatedBy);

  List<ProductionDailyResponseDto> getDailyProductions();

  List<ProductionHistoryResponseDto> getCompletedAndIncompleteOrders();

  ProductionStatusUpdateResponseDto cambiarEstadoPendienteAEnProceso(Long id, ProductionStatusUpdateRequestDto dto);

  ProductionFinalizeResponseDto finalizarProduccion(Long productionId, ProductionFinalizeRequestDto request);

}