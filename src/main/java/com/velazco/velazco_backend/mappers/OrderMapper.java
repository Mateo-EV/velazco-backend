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
import com.velazco.velazco_backend.entities.User;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    List<OrderListResponseDto> toListResponse(List<Order> orders);

    default OrderListResponseDto.AttendedBy mapUserToAttendedBy(User user) {
        if (user == null)
            return null;

        return OrderListResponseDto.AttendedBy.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    @Mapping(target = "attendedBy", expression = "java(mapUserToAttendedBy(order.getAttendedBy()))")
    OrderListResponseDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "attendedBy", ignore = true)
    Order toEntity(OrderStartRequestDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    OrderDetail toEntity(OrderStartRequestDto.DetailOrderStartRequestDto detailDto);

    OrderStartResponseDto toStartResponse(Order order);

    OrderConfirmSaleResponseDto toConfirmSaleResponse(Order order);
}
