package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.services.CategoryService;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category createCategory(Category category) {
    return categoryRepository.save(category);
  }

  @Override
  public void deleteCategoryById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    categoryRepository.delete(category);
  }

}