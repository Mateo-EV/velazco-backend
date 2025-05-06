package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.services.CategoryService;

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

}