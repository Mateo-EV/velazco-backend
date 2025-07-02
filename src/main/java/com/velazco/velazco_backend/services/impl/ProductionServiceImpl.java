package com.velazco.velazco_backend.services.impl;

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
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Production;
import com.velazco.velazco_backend.entities.ProductionDetail;
import com.velazco.velazco_backend.entities.ProductionDetailId;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.entities.Production.ProductionStatus;
import com.velazco.velazco_backend.mappers.ProductionMapper;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.repositories.ProductionRepository;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.services.ProductionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {
  private final ProductionRepository productionRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  private final ProductionMapper productionMapper;

  @Override
  @Transactional(readOnly = true)
  public List<ProductionPendingResponseDto> getPendingProductions() {
    List<Production> pending = productionRepository.findByStatus(ProductionStatus.PENDIENTE);

    return pending.stream().map(production -> {
      ProductionPendingResponseDto dto = productionMapper.toPendingDto(production);
      dto.setDetails(productionMapper.toDetailDtoPendingList(production.getDetails()));
      return dto;
    }).toList();
  }

  public List<ProductionHistoryResponseDto> getCompletedAndIncompleteOrders() {
    List<Production.ProductionStatus> estados = List.of(
        Production.ProductionStatus.COMPLETO,
        Production.ProductionStatus.INCOMPLETO);

    List<Production> ordenes = productionRepository.findByStatusIn(estados);

    return ordenes.stream()
        .map(production -> {
          ProductionHistoryResponseDto dto = productionMapper.toHistoryDto(production);
          dto.setProducts(productionMapper.toProductDetailList(production.getDetails()));
          return dto;
        })
        .toList();
  }

  @Override
  public ProductionCreateResponseDto createProduction(ProductionCreateRequestDto request, User assignedBy) {
    LocalDate requestedDate = request.getProductionDate();
    LocalDate today = LocalDate.now();

    if (requestedDate.isBefore(today)) {
      throw new IllegalArgumentException("No se puede crear una producción en una fecha pasada.");
    }

    boolean existsOnSameDate = productionRepository.existsByProductionDate(requestedDate);
    if (existsOnSameDate) {
      throw new IllegalArgumentException("Ya existe una orden de producción para la fecha seleccionada.");
    }
    Production production = productionMapper.toEntity(request);
    production.setAssignedBy(assignedBy);

    User assignedTo = userRepository.findById(production.getAssignedTo().getId())
        .orElseThrow(() -> new EntityNotFoundException("User to assign not found"));

    production.setAssignedTo(assignedTo);

    List<Product> products = productRepository
        .findAllById(
            production.getDetails()
                .stream()
                .map(detail -> detail.getProduct().getId())
                .toList());

    for (ProductionDetail detail : production.getDetails()) {
      Product product = products.stream()
          .filter(p -> p.getId().equals(detail.getProduct().getId()))
          .findFirst()
          .orElseThrow(() -> new EntityNotFoundException("Product not found"));

      detail.setProduct(product);
      detail.setProduction(production);
      detail.setProducedQuantity(0);
      detail.setId(ProductionDetailId.builder().productId(product.getId()).build());
    }

    Production savedProduction = productionRepository.save(production);
    return productionMapper.toCreateResponseDto(savedProduction);
  }

  @Override
  public void deleteProductionById(Long productionId) {
    Production production = productionRepository.findById(productionId)
        .orElseThrow(() -> new EntityNotFoundException("Production not found"));

    productionRepository.delete(production);
  }

  @Override
  public ProductionUpdateResponseDto updateProduction(Long id, ProductionUpdateRequestDto dto, User updatedBy) {
    Production existing = productionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Production not found with ID: " + id));

    User assignedTo = userRepository.findById(dto.getAssignedToId())
        .orElseThrow(() -> new EntityNotFoundException("Assigned user not found with ID: " + dto.getAssignedToId()));

    existing.setProductionDate(dto.getProductionDate());
    existing.setStatus(dto.getStatus());
    existing.setAssignedTo(assignedTo);
    existing.setComments(dto.getComments());

    Map<Long, ProductionDetail> existingDetails = existing.getDetails().stream()
        .collect(Collectors.toMap(
            detail -> detail.getProduct().getId(),
            detail -> detail));

    Set<Long> requestedProductIds = dto.getDetails().stream()
        .map(ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto::getProductId)
        .collect(Collectors.toSet());

    existing.getDetails().removeIf(detail -> !requestedProductIds.contains(detail.getProduct().getId()));

    for (ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto detailDto : dto.getDetails()) {
      ProductionDetail detail = existingDetails.get(detailDto.getProductId());

      if (detail != null) {
        detail.setRequestedQuantity(detailDto.getRequestedQuantity());
      } else {
        Product product = productRepository.findById(detailDto.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + detailDto.getProductId()));

        ProductionDetail newDetail = new ProductionDetail();
        newDetail.setId(ProductionDetailId.builder().productId(product.getId()).build());
        newDetail.setProduction(existing);
        newDetail.setProduct(product);
        newDetail.setRequestedQuantity(detailDto.getRequestedQuantity());
        newDetail.setProducedQuantity(0);

        existing.getDetails().add(newDetail);
      }
    }

    Production savedProduction = productionRepository.save(existing);
    return productionMapper.toUpdateResponseDto(savedProduction);
  }

  @Override
  public List<ProductionDailyResponseDto> getDailyProductions() {
    List<Production> productions = productionRepository.findProductionsByProductionDate(LocalDate.now());

    return productions.stream()
        .map(production -> {
          ProductionDailyResponseDto dto = productionMapper.toDailyResponseDto(production);
          dto.setDetails(productionMapper.toDailyDetailDtoList(production.getDetails()));
          return dto;
        })
        .toList();
  }

  @Override
  @Transactional
  public ProductionStatusUpdateResponseDto cambiarEstadoPendienteAEnProceso(Long id,
      ProductionStatusUpdateRequestDto dto) {
    Production production = productionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Orden de producción no encontrada"));

    Production.ProductionStatus estadoActual = production.getStatus();
    if (estadoActual != Production.ProductionStatus.PENDIENTE) {
      throw new IllegalStateException("Solo se puede cambiar de PENDIENTE a EN_PROCESO");
    }

    if (!"EN_PROCESO".equals(dto.getNuevoEstado())) {
      throw new IllegalArgumentException("El nuevo estado permitido es únicamente EN_PROCESO");
    }

    production.setStatus(Production.ProductionStatus.EN_PROCESO);
    Production actualizado = productionRepository.save(production);

    return ProductionStatusUpdateResponseDto.builder()
        .id(actualizado.getId())
        .estadoAnterior(estadoActual.name())
        .estadoActual(actualizado.getStatus().name())
        .build();
  }

  @Override
  @Transactional
  public ProductionFinalizeResponseDto finalizarProduccion(Long productionId, ProductionFinalizeRequestDto request) {
    Production production = productionRepository.findById(productionId)
        .orElseThrow(() -> new EntityNotFoundException("Producción no encontrada"));

    if (production.getStatus() != ProductionStatus.EN_PROCESO) {
      throw new IllegalStateException("Solo se puede finalizar una producción que esté EN_PROCESO.");
    }

    Map<Long, ProductionDetail> detailMap = production.getDetails().stream()
        .collect(Collectors.toMap(detail -> detail.getProduct().getId(), detail -> detail));

    List<ProductionFinalizeResponseDto.ProductResult> resultados = new ArrayList<>();
    boolean todosCompletos = true;

    for (ProductionFinalizeRequestDto.ProductResultDto dto : request.getProductos()) {
      ProductionDetail detail = detailMap.get(dto.getProductId());

      if (detail == null) {
        throw new EntityNotFoundException(
            "Producto con ID " + dto.getProductId() + " no encontrado en esta producción.");
      }

      detail.setProducedQuantity(dto.getProducedQuantity());

      boolean completo = dto.getProducedQuantity() >= detail.getRequestedQuantity();
      if (!completo) {
        todosCompletos = false;
        detail.setComments(dto.getMotivoIncompleto());
      } else {
        detail.setComments(null); // Limpia comentarios si está completo
      }

      // 👉 ACTUALIZAR STOCK INDIVIDUALMENTE
      Product product = detail.getProduct();
      int nuevoStock = product.getStock() + detail.getProducedQuantity();
      product.setStock(nuevoStock);
      productRepository.save(product);

      resultados.add(ProductionFinalizeResponseDto.ProductResult.builder()
          .productId(dto.getProductId())
          .cantidadProducida(dto.getProducedQuantity())
          .motivo(dto.getMotivoIncompleto())
          .build());
    }

    // 👉 SI TODOS CUMPLIERON, COMPLETO; SINO, INCOMPLETO
    production.setStatus(todosCompletos ? ProductionStatus.COMPLETO : ProductionStatus.INCOMPLETO);
    productionRepository.save(production);

    return ProductionFinalizeResponseDto.builder()
        .productionId(production.getId())
        .estadoFinal(production.getStatus().name())
        .productos(resultados)
        .build();
  }

}