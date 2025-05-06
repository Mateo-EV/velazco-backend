package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.Role;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.OrderMapper;
import com.velazco.velazco_backend.services.OrderService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Test
    @WithMockUser
    void shouldGetAllOrdersSuccessfully() throws Exception {

        // Arrange
        Order orderEntity = new Order();
        orderEntity.setId(1L);
        orderEntity.setDate(LocalDate.parse("2023-10-01").atStartOfDay());
        orderEntity.setClientName("John Doe");
        orderEntity.setStatus(Order.OrderStatus.PENDIENTE);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setName("Mateo Velazco");
        user.setRole(role);
        user.setActive(true);
        user.setEmail("mateo@gmail.com");
        user.setHashedPassword("123");

        orderEntity.setAttendedBy(user);

        List<Order> orders = List.of(orderEntity);
        Page<Order> orderPage = new PageImpl<>(orders);

        OrderListResponseDto dto = OrderListResponseDto.builder()
                .id(1L)
                .date(orderEntity.getDate())
                .clientName(orderEntity.getClientName())
                .status(orderEntity.getStatus().name())
                .attendedBy(OrderListResponseDto.AttendedBy.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .build();

        List<OrderListResponseDto> dtoList = List.of(dto);

        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toListResponse(orders)).thenReturn(dtoList);

        // Act + Assert
        mockMvc.perform(get("/api/orders")
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].status").value("PENDIENTE"))
                .andExpect(jsonPath("$.content[0].date").exists())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

}
