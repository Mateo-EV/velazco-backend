package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    List<CategoryListResponseDto> toListResponse(List<Category> category);
}
