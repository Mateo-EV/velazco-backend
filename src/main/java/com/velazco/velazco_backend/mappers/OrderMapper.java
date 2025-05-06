package com.velazco.velazco_backend.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.entities.Order;
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
}
