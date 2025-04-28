package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.entities.Product;

public interface ProductService {

  List<Product> getAllProducts();

  Product createProduct(Product product);

  void deleteProductById(Long id);

  Product updateProductActive(Long id, Boolean active);
}
