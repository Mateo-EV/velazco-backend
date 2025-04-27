package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}