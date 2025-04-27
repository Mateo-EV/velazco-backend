package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.CategoryRepository;
import com.velazco.velazco_backend.services.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }
}