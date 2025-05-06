package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.dto.category.requests.CategoryCreateRequestDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.mappers.CategoryMapper;
import com.velazco.velazco_backend.services.CategoryService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
    this.categoryMapper = categoryMapper;
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<List<CategoryListResponseDto>> getAllCategories() {
    List<Category> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categoryMapper.toListResponse(categories));
  }

  @PostMapping
  public ResponseEntity<CategoryCreateResponseDto> createCategory(
      @Valid @RequestBody CategoryCreateRequestDto requestDTO) {
    Category category = categoryMapper.toEntity(requestDTO);

    Category savedCategory = categoryService.createCategory(category);

    return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toCreateResponse(savedCategory));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategoryById(id);
    return ResponseEntity.noContent().build();
  }
}