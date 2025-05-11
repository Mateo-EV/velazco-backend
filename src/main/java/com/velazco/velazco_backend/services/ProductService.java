package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.entities.Product;

public interface ProductService {

  List<Product> getAllProducts();

  List<ProductListResponseDto> getAllAvailableProducts();

  Product createProduct(Product product);

  Product updateProduct(Long id, Product product);

  void deleteProductById(Long id);

  Product updateProductActive(Long id, Boolean active);

}
