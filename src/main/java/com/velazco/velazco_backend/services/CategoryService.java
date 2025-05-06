package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.entities.Category;

public interface CategoryService {

    List<Category> getAllCategories();

    Category createCategory(Category category);

    void deleteCategoryById(Long id);

}