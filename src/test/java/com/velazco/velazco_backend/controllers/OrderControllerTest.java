package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.OrderDetail;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Role;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.OrderMapper;
import com.velazco.velazco_backend.services.OrderService;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldGetOrdersByStatusSuccessfully() throws Exception {
        // Arrange
        Order orderEntity = new Order();
        orderEntity.setId(1L);
        orderEntity.setDate(LocalDate.parse("2023-10-01").atStartOfDay());
        orderEntity.setClientName("John Doe");
        orderEntity.setStatus(Order.OrderStatus.PAGADO);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setName("Mateo Velazco");
        user.setRole(role);
        user.setActive(true);
        user.setEmail("mateo@gmail.com");

        orderEntity.setAttendedBy(user);

        OrderListResponseDto.AttendedByListResponseDto attendedByDto = OrderListResponseDto.AttendedByListResponseDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .build();

        OrderListResponseDto dto = OrderListResponseDto.builder()
                .id(1L)
                .date(orderEntity.getDate())
                .clientName(orderEntity.getClientName())
                .status(orderEntity.getStatus().name())
                .attendedBy(attendedByDto)
                .build();

        List<OrderListResponseDto> dtoList = List.of(dto);

        PaginatedResponseDto<OrderListResponseDto> paginatedResponse = PaginatedResponseDto
                .<OrderListResponseDto>builder()
                .content(dtoList)
                .currentPage(0)
                .totalItems(1)
                .totalPages(1)
                .build();

        when(orderService.getOrdersByStatus(eq(Order.OrderStatus.PAGADO), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // Act + Assert
        mockMvc.perform(get("/api/orders/status/pagado")
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].status").value("PAGADO"))
                .andExpect(jsonPath("$.content[0].attendedBy.name").value("Mateo Velazco"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @WithMockUser
    void shouldStartOrderSuccessfully() throws Exception {
        OrderStartRequestDto requestDto = OrderStartRequestDto.builder()
                .clientName("John Doe")
                .details(List.of(
                        new OrderStartRequestDto.DetailOrderStartRequestDto(1L, 2),
                        new OrderStartRequestDto.DetailOrderStartRequestDto(3L, 1)))
                .build();

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("cliente@correo.com");

        Order mappedEntity = new Order();
        mappedEntity.setClientName("John Doe");
        mappedEntity.setDetails(List.of(
                OrderDetail.builder()
                        .product(Product.builder().id(1L).build())
                        .quantity(2)
                        .build(),
                OrderDetail.builder()
                        .product(Product.builder().id(3L).build())
                        .quantity(1)
                        .build()));

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setDate(LocalDateTime.now());
        savedOrder.setAttendedBy(mockUser);
        savedOrder.setClientName("John Doe");
        savedOrder.setStatus(Order.OrderStatus.PENDIENTE);
        savedOrder.setDetails(List.of(
                OrderDetail.builder()
                        .product(Product.builder().id(1L).price(BigDecimal.valueOf(10.0)).build())
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(10.0))
                        .build(),
                OrderDetail.builder()
                        .product(Product.builder().id(3L).price(BigDecimal.valueOf(20.0)).build())
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(20.0))
                        .build()));

        OrderStartResponseDto responseDto = OrderStartResponseDto.builder()
                .id(savedOrder.getId())
                .date(savedOrder.getDate())
                .clientName(savedOrder.getClientName())
                .status(savedOrder.getStatus().name())
                .attendedBy(OrderStartResponseDto.AttendedByOrderStartResponseDto.builder()
                        .id(mockUser.getId())
                        .name(mockUser.getName())
                        .build())
                .details(List.of(
                        OrderStartResponseDto.DetailOrderStartResponseDto.builder()
                                .product(OrderStartResponseDto.ProductOrderStartResponseDto.builder().id(1L).build())
                                .quantity(2)
                                .unitPrice(BigDecimal.valueOf(10.0))
                                .build(),
                        OrderStartResponseDto.DetailOrderStartResponseDto.builder()
                                .product(OrderStartResponseDto.ProductOrderStartResponseDto.builder().id(3L).build())
                                .quantity(1)
                                .unitPrice(BigDecimal.valueOf(20.0))
                                .build()))
                .build();

        Mockito.when(orderMapper.toEntity(eq(requestDto))).thenReturn(mappedEntity);
        Mockito.when(orderService.startOrder(eq(mockUser), eq(requestDto))).thenReturn(responseDto);

        mockMvc.perform(post("/api/orders/start").with(csrf()).with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDIENTE"))
                .andExpect(jsonPath("$.attendedBy.id").value(mockUser.getId()))
                .andExpect(jsonPath("$.attendedBy.name").value(mockUser.getName()))
                .andExpect(jsonPath("$.details[0].product.id").value(1L))
                .andExpect(jsonPath("$.details[0].quantity").value(2))
                .andExpect(jsonPath("$.details[0].unitPrice").value(10.0))
                .andExpect(jsonPath("$.details[1].product.id").value(3L))
                .andExpect(jsonPath("$.details[1].quantity").value(1))
                .andExpect(jsonPath("$.details[1].unitPrice").value(20.0));
    }
}