package com.velazco.velazco_backend.controllers;

import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.dto.category.requests.CategoryUpdateRequestDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryUpdateResponseDto;
import com.velazco.velazco_backend.dto.category.requests.CategoryCreateRequestDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.services.CategoryService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping
  public ResponseEntity<List<CategoryListResponseDto>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @PreAuthorize("hasRole('Administrador')")
  @PostMapping
  public ResponseEntity<CategoryCreateResponseDto> createCategory(
      @Valid @RequestBody CategoryCreateRequestDto createRequest) {
    CategoryCreateResponseDto responseDTO = categoryService.createCategory(createRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
  }

  @PreAuthorize("hasRole('Administrador')")
  @PutMapping("/{id}")
  public ResponseEntity<CategoryUpdateResponseDto> updateCategory(
      @PathVariable Long id,
      @Valid @RequestBody CategoryUpdateRequestDto updateRequest) {
    CategoryUpdateResponseDto responseDTO = categoryService.updateCategory(id, updateRequest);
    return ResponseEntity.ok(responseDTO);
  }

  @PreAuthorize("hasRole('Administrador')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategoryById(id);
    return ResponseEntity.noContent().build();
  }
}