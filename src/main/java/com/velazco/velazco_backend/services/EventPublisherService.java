package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryUpdateResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionFinalizeResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.dto.user.response.UserUpdateResponseDto;
import com.velazco.velazco_backend.entities.Sale;
import com.velazco.velazco_backend.entities.Dispatch;
import com.velazco.velazco_backend.entities.Product;

public interface EventPublisherService {
  // Product events
  void publishProductCreated(ProductCreateResponseDto product);

  void publishProductUpdated(ProductUpdateResponseDto product);

  void publishProductActiveUpdated(ProductUpdateActiveResponseDto product);

  void publishProductDeleted(ProductCreateResponseDto product);

  void publishProductStockChanged(Product product, String reason); // For stock changes

  // Category events
  void publishCategoryCreated(CategoryCreateResponseDto category);

  void publishCategoryUpdated(CategoryUpdateResponseDto category);

  void publishCategoryDeleted(CategoryCreateResponseDto category);

  // Order events
  void publishOrderStarted(OrderStartResponseDto order); // Changed from Created

  void publishOrderSaleConfirmed(OrderConfirmSaleResponseDto order);

  void publishOrderDispatchConfirmed(OrderConfirmDispatchResponseDto order);

  void publishOrderCancelled(OrderStartResponseDto order);

  // Production events
  void publishProductionCreated(ProductionCreateResponseDto production);

  void publishProductionUpdated(ProductionUpdateResponseDto production);

  void publishProductionFinalized(ProductionFinalizeResponseDto production);

  void publishProductionDeleted(ProductionCreateResponseDto production);

  // User events
  void publishUserCreated(UserCreateResponseDto user);

  void publishUserUpdated(UserUpdateResponseDto user);

  void publishUserDeleted(UserCreateResponseDto user);

  // Sale events
  void publishSaleCreated(Sale sale);

  // Dispatch events
  void publishDispatchCreated(Dispatch dispatch);
}
