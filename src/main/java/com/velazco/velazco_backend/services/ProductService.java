package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.entities.Product;

public interface ProductService {

  public Product createProduct(Product product);

  void deleteProductById(Long id);

  Product updateProductActive(Long id, Boolean active);
}
