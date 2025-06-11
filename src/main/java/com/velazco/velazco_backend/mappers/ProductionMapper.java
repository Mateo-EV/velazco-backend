package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
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

  ProductionCreateResponseDto toCreateResponseDto(Production production);

  ProductionUpdateResponseDto toUpdateResponseDto(Production production);

  List<ProductionListResponseDto> toListResponseDto(List<Production> productions);
}
