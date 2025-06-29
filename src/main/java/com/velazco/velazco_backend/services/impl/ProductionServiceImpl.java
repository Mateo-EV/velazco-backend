package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionListResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Production;
import com.velazco.velazco_backend.entities.ProductionDetail;
import com.velazco.velazco_backend.entities.ProductionDetailId;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.ProductionMapper;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.repositories.ProductionRepository;
import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.services.ProductionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {
  private final ProductionRepository productionRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  private final ProductionMapper productionMapper;

  @Override
  public List<ProductionCreateResponseDto> getAllProductions() {
    List<Production> productions = productionRepository.findAll();
    return productions.stream()
        .map(productionMapper::toCreateResponseDto)
        .toList();
  }

  public List<ProductionHistoryResponseDto> getCompletedAndIncompleteOrders() {
    List<Production.ProductionStatus> estados = List.of(
        Production.ProductionStatus.COMPLETO,
        Production.ProductionStatus.INCOMPLETO);

    List<Production> ordenes = productionRepository.findByStatusIn(estados);

    return ordenes.stream()
        .map(production -> {
          ProductionHistoryResponseDto dto = productionMapper.toHistoryDto(production);
          List<ProductionHistoryResponseDto.ProductDetail> productDetails = production.getDetails().stream()
              .map(detail -> ProductionHistoryResponseDto.ProductDetail.builder()
                  .productName(detail.getProduct().getName())
                  .quantity(detail.getProducedQuantity())
                  .build())
              .toList();
          dto.setProducts(productDetails);
          return dto;
        })
        .toList();
  }

  @Override
  public ProductionCreateResponseDto createProduction(ProductionCreateRequestDto request, User assignedBy) {
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

    Map<Long, ProductionDetail> existingDetails = existing.getDetails().stream()
        .collect(Collectors.toMap(
            detail -> detail.getProduct().getId(),
            detail -> detail));

    Set<Long> requestedProductIds = dto.getDetails().stream()
        .map(ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto::getProductId)
        .collect(Collectors.toSet());

    // ESTA LÍNEA ES CLAVE: elimina los detalles que no están en el request
    existing.getDetails().removeIf(detail -> !requestedProductIds.contains(detail.getProduct().getId()));

    for (ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto detailDto : dto.getDetails()) {
      ProductionDetail detail = existingDetails.get(detailDto.getProductId());

      if (detail != null) {
        detail.setRequestedQuantity(detailDto.getRequestedQuantity());
        detail.setComments(detailDto.getComments());
      } else {
        Product product = productRepository.findById(detailDto.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + detailDto.getProductId()));

        ProductionDetail newDetail = new ProductionDetail();
        newDetail.setId(ProductionDetailId.builder().productId(product.getId()).build());
        newDetail.setProduction(existing);
        newDetail.setProduct(product);
        newDetail.setRequestedQuantity(detailDto.getRequestedQuantity());
        newDetail.setProducedQuantity(0);
        newDetail.setComments(detailDto.getComments());

        existing.getDetails().add(newDetail);
      }
    }

    Production savedProduction = productionRepository.save(existing);

    return productionMapper.toUpdateResponseDto(savedProduction);
  }

  @Override
  public List<ProductionListResponseDto> getDailyProductions() {
    List<Production> productions = productionRepository.findProductionsByProductionDate(LocalDate.now());

    return productionMapper.toListResponseDto(productions);
  }
}