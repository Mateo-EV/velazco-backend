package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;

public interface EventPublisherService {
  void publishProductCreated(ProductCreateResponseDto product);
}
