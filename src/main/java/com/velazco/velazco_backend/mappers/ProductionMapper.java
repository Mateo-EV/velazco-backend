package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionListResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.entities.Production;
import com.velazco.velazco_backend.entities.ProductionDetail;

@Mapper(componentModel = "spring")
public interface ProductionMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "assignedBy", ignore = true)
  @Mapping(target = "assignedTo.id", source = "assignedToId")
  Production toEntity(ProductionCreateRequestDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "producedQuantity", ignore = true)
  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "production", ignore = true)
  ProductionDetail toEntity(ProductionCreateRequestDto.ProductionDetailCreateRequestDto detailDto);

  @Mapping(source = "id", target = "orderNumber", qualifiedByName = "orderNumberFormat")
  @Mapping(source = "productionDate", target = "date")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "assignedTo.name", target = "responsible")
  @Mapping(target = "products", ignore = true)
  ProductionHistoryResponseDto toHistoryDto(Production production);

  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "producedQuantity", target = "quantity")
  ProductionHistoryResponseDto.ProductDetail toProductDetail(ProductionDetail detail);

  List<ProductionHistoryResponseDto.ProductDetail> toProductDetailList(List<ProductionDetail> details);

  @Named("orderNumberFormat")
  default String mapOrderNumber(Long id) {
    return String.format("OP-%04d", id); 
  }

  ProductionCreateResponseDto toCreateResponseDto(Production production);
  ProductionUpdateResponseDto toUpdateResponseDto(Production production);
  List<ProductionListResponseDto> toListResponseDto(List<Production> productions);
}
