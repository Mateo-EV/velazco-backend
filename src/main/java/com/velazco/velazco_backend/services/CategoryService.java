package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.category.requests.CategoryCreateRequestDto;
import com.velazco.velazco_backend.dto.category.requests.CategoryUpdateRequestDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryUpdateResponseDto;
import com.velazco.velazco_backend.entities.Category;

public interface CategoryService {

    List<CategoryListResponseDto> getAllCategories();

    Category getCategoryById(Long id);

    CategoryCreateResponseDto createCategory(CategoryCreateRequestDto dto);

    CategoryUpdateResponseDto updateCategory(Long id, CategoryUpdateRequestDto dto);

    void deleteCategoryById(Long id);

}