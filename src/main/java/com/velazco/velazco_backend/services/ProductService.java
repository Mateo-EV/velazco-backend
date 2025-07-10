package com.velazco.velazco_backend.services;

import java.util.List;

import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductLowStockResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;

public interface ProductService {

  List<ProductListResponseDto> getAllProducts();

  List<ProductListResponseDto> getAllAvailableProducts();

  ProductCreateResponseDto createProduct(ProductCreateRequestDto dto);

  ProductUpdateResponseDto updateProduct(Long id, ProductUpdateRequestDto dto);

  void deleteProductById(Long id);

  ProductUpdateActiveResponseDto updateProductActive(Long id, Boolean active);

  ProductLowStockResponseDto getLowStockProducts();

}
