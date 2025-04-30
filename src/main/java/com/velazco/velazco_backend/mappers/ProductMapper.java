package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
import com.velazco.velazco_backend.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  ProductCreateResponseDto toCreateResponse(Product product);

  List<ProductListResponseDto> toListResponse(List<Product> product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "orderDetails", ignore = true)
  @Mapping(target = "productionDetails", ignore = true)
  @Mapping(target = "category.id", source = "categoryId")
  Product toEntity(ProductCreateRequestDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "orderDetails", ignore = true)
  @Mapping(target = "productionDetails", ignore = true)
  @Mapping(target = "category.id", source = "categoryId")
  Product toEntity(ProductUpdateRequestDto dto);

  ProductUpdateResponseDto toUpdateResponse(Product product);

  ProductUpdateActiveResponseDto toUpdateActiveResponse(Product product);
}
