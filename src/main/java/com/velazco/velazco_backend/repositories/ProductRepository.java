package com.velazco.velazco_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velazco.velazco_backend.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE p.active = true AND p.stock > 0")
  List<Product> findAvailableProducts();

  @Modifying
  @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
  int decrementStock(@Param("productId") Long productId, @Param("quantity") int quantity);

  @Modifying
  @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :productId")
  int restoreStock(@Param("productId") Long productId, @Param("quantity") int quantity);

}