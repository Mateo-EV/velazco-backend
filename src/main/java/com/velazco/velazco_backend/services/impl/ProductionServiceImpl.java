package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
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

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {
  private final ProductionRepository productionRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  private final ProductionMapper productionMapper;

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
}