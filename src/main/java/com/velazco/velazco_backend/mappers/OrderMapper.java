package com.velazco.velazco_backend.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.OrderDetail;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    List<OrderListResponseDto> toListResponse(List<Order> orders);

    @Mapping(target = "attendedBy.id", source = "attendedBy.id")
    @Mapping(target = "attendedBy.name", source = "attendedBy.name")
    OrderListResponseDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "attendedBy", ignore = true)
    @Mapping(target = "sale", ignore = true)
    Order toEntity(OrderStartRequestDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    OrderDetail toEntity(OrderStartRequestDto.DetailOrderStartRequestDto detailDto);

    OrderStartResponseDto toStartResponse(Order order);

    OrderConfirmSaleResponseDto toConfirmSaleResponse(Order order);
}
