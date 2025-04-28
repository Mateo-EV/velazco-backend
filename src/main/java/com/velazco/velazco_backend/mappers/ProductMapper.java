package com.velazco.velazco_backend.mappers;

import org.mapstruct.Mapper;

import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  ProductUpdateActiveResponseDto toUpdateActiveResponse(Product product);
}
