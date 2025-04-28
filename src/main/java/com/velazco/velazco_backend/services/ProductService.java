package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.entities.Product;

public interface ProductService {
  void deleteProductById(Long id);

  Product updateProductActive(Long id, Boolean active);
}
