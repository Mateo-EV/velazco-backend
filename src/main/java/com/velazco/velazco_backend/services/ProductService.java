package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.entities.Product;

public interface ProductService {

  List<Product> getAllProducts();

  List<ProductListResponseDto> getAllAvailableProducts();

  ProductCreateResponseDto createProductWithImage(ProductCreateRequestDto dto);

  public Product updateProduct(Long id, ProductUpdateRequestDto dto);

  void deleteProductById(Long id);

  Product updateProductActive(Long id, Boolean active);

}
