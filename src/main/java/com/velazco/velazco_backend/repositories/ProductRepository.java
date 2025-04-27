package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}