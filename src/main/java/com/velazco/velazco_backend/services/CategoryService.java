package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.entities.Category;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategoryById(Long id);

}