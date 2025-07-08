package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.dto.category.requests.CategoryCreateRequestDto;
import com.velazco.velazco_backend.dto.category.requests.CategoryUpdateRequestDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryUpdateResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.mappers.CategoryMapper;
import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.services.CategoryService;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
  }

  @Override
  public List<CategoryListResponseDto> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    return categoryMapper.toListResponse(categories);
  }

  @Override
  public CategoryCreateResponseDto createCategory(CategoryCreateRequestDto dto) {
    Category category = categoryMapper.toEntity(dto);
    Category saved = categoryRepository.save(category);
    return categoryMapper.toCreateResponse(saved);
  }

  @Override
  public CategoryUpdateResponseDto updateCategory(Long id, CategoryUpdateRequestDto dto) {
    Category existing = getCategoryById(id);
    existing.setName(dto.getName());
    Category updated = categoryRepository.save(existing);
    return categoryMapper.toUpdateResponse(updated);
  }

  @Override
  public void deleteCategoryById(Long id) {
    Category category = getCategoryById(id);
    categoryRepository.delete(category);
  }
}