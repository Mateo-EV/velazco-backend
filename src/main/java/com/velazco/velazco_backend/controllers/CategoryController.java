package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.mappers.CategoryMapper;
import com.velazco.velazco_backend.services.CategoryService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}