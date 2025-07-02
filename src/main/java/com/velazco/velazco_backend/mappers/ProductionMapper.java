package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionDailyResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionPendingResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.entities.Production;
import com.velazco.velazco_backend.entities.ProductionDetail;

@Mapper(componentModel = "spring")
public interface ProductionMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "assignedBy", ignore = true)
  @Mapping(target = "assignedTo.id", source = "assignedToId")
  @Mapping(source = "comments", target = "comments")
  Production toEntity(ProductionCreateRequestDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "producedQuantity", ignore = true)
  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "production", ignore = true)
  @Mapping(target = "comments", ignore = true)
  ProductionDetail toEntity(ProductionCreateRequestDto.ProductionDetailCreateRequestDto detailDto);

  @Mapping(source = "id", target = "orderNumber", qualifiedByName = "orderNumberFormat")
  @Mapping(source = "productionDate", target = "date")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "assignedTo.name", target = "responsible")
  @Mapping(target = "products", ignore = true)
  ProductionHistoryResponseDto toHistoryDto(Production production);

@Mapping(source = "product.name", target = "productName")
@Mapping(source = "producedQuantity", target = "producedQuantity")
@Mapping(source = "requestedQuantity", target = "requestedQuantity")
@Mapping(source = "comments", target = "comments")
ProductionHistoryResponseDto.ProductDetail toProductDetail(ProductionDetail detail);

  List<ProductionHistoryResponseDto.ProductDetail> toProductDetailList(List<ProductionDetail> details);

  @Mapping(source = "product.id", target = "product.id")
  @Mapping(source = "product.name", target = "product.name")
  @Mapping(source = "requestedQuantity", target = "requestedQuantity")
  @Mapping(source = "producedQuantity", target = "producedQuantity")
  ProductionDailyResponseDto.DetailDto toDailyDetailDto(ProductionDetail detail);

  List<ProductionDailyResponseDto.DetailDto> toDailyDetailDtoList(List<ProductionDetail> details);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  ProductionDailyResponseDto.AssignedByDto toAssignedByDto(com.velazco.velazco_backend.entities.User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  ProductionDailyResponseDto.AssignedToDto toAssignedToDto(com.velazco.velazco_backend.entities.User user);
  
  ProductionCreateResponseDto toCreateResponseDto(Production production);

  ProductionUpdateResponseDto toUpdateResponseDto(Production production);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "productionDate", target = "productionDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "assignedBy", target = "assignedBy")
  @Mapping(source = "assignedTo", target = "assignedTo")
  @Mapping(source = "comments", target = "comments")
  @Mapping(target = "details", ignore = true)
  ProductionDailyResponseDto toDailyResponseDto(Production production);

  List<ProductionDailyResponseDto> toDailyResponseDtoList(List<Production> productions);

  @Named("orderNumberFormat")
  default String mapOrderNumber(Long id) {
    return String.format("OP-%04d", id);
  }


  @Mapping(source = "id", target = "id")
  @Mapping(source = "productionDate", target = "productionDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "assignedBy", target = "assignedBy")
  @Mapping(source = "assignedTo", target = "assignedTo")
  @Mapping(source = "comments", target = "comments")
  @Mapping(target = "details", ignore = true)
  ProductionPendingResponseDto toPendingDto(Production production);

  @Mapping(source = "product.id", target = "product.id")
  @Mapping(source = "product.name", target = "product.name")
  @Mapping(source = "requestedQuantity", target = "requestedQuantity")
  @Mapping(source = "producedQuantity", target = "producedQuantity")
  ProductionPendingResponseDto.DetailDto toPendingDetailDto(ProductionDetail detail);

  List<ProductionPendingResponseDto.DetailDto> toDetailDtoPendingList(List<ProductionDetail> details);

}