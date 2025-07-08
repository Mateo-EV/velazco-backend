package com.velazco.velazco_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderConfirmSaleRequestDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.DeliveredOrderResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.entities.Order;
import com.velazco.velazco_backend.entities.OrderDetail;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.OrderMapper;
import com.velazco.velazco_backend.repositories.ProductRepository;
import com.velazco.velazco_backend.services.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductRepository productRepository; // asegúrate de tenerlo

    @Test
    @WithMockUser
    void shouldGetOrdersByStatusSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Mateo Velazco");

        OrderListResponseDto.AttendedByListResponseDto attendedByDto = new OrderListResponseDto.AttendedByListResponseDto(
                user.getId(), user.getName());

        OrderListResponseDto.DetailsOrderResponseDto detailDto = new OrderListResponseDto.DetailsOrderResponseDto(2,
                BigDecimal.valueOf(10.0),
                new OrderStartResponseDto.ProductOrderStartResponseDto(1L, "Producto 1"));

        OrderListResponseDto dto = OrderListResponseDto.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .clientName("John Doe")
                .status(Order.OrderStatus.PAGADO.name())
                .attendedBy(attendedByDto)
                .details(List.of(detailDto))
                .build();

        PaginatedResponseDto<OrderListResponseDto> paginatedResponse = PaginatedResponseDto
                .<OrderListResponseDto>builder()
                .content(List.of(dto))
                .currentPage(0)
                .totalItems(1)
                .totalPages(1)
                .build();

        Mockito.when(orderService.getOrdersByStatus(eq(Order.OrderStatus.PAGADO), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        mockMvc.perform(get("/api/orders/status/pagado")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].status").value("PAGADO"))
                .andExpect(jsonPath("$.content[0].attendedBy.name").value("Mateo Velazco"))
                .andExpect(jsonPath("$.content[0].details[0].product.id").value(1L))
                .andExpect(jsonPath("$.content[0].details[0].quantity").value(2))
                .andExpect(jsonPath("$.content[0].details[0].unitPrice").value(10.0))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @WithMockUser
    void shouldStartOrderSuccessfully() throws Exception {

        // Datos de entrada del cliente
        OrderStartRequestDto requestDto = OrderStartRequestDto.builder()
                .clientName("John Doe")
                .details(List.of(
                        new OrderStartRequestDto.DetailOrderStartRequestDto(1L, 2)))
                .build();

        // Usuario autenticado simulando al que atiende la orden
        User user = new User();
        user.setId(10L);
        user.setName("Mateo Velazco");

        // Producto simulado
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Producto 1");
        mockProduct.setPrice(BigDecimal.valueOf(10.0));

        // Detalle de pedido simulado
        OrderDetail detail = new OrderDetail();
        detail.setProduct(mockProduct);
        detail.setQuantity(2);

        Order orderEntity = new Order();
        orderEntity.setDetails(List.of(detail));

        // 🧠 Mocks necesarios para evitar NullPointer y error 500
        Mockito.when(orderMapper.toEntity(eq(requestDto))).thenReturn(orderEntity);
        Mockito.when(productRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(mockProduct));
        Mockito.when(productRepository.decrementStock(1L, 2))
                .thenReturn(1); // Simula stock suficiente

        // Respuesta simulada del servicio
        OrderStartResponseDto responseDto = OrderStartResponseDto.builder()
                .id(100L)
                .date(LocalDateTime.now())
                .clientName("John Doe")
                .status(Order.OrderStatus.PENDIENTE.name())
                .attendedBy(OrderStartResponseDto.AttendedByOrderStartResponseDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .details(List.of(
                        OrderStartResponseDto.DetailOrderStartResponseDto.builder()
                                .product(OrderStartResponseDto.ProductOrderStartResponseDto.builder()
                                        .id(1L)
                                        .name("Producto 1")
                                        .build())
                                .quantity(2)
                                .unitPrice(BigDecimal.valueOf(10.0))
                                .build()))
                .build();

        Mockito.when(orderService.startOrder(eq(user), eq(requestDto)))
                .thenReturn(responseDto);

        // Ejecutar el endpoint y verificar resultado
        mockMvc.perform(post("/api/orders/start")
                .with(csrf())
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDIENTE"))
                .andExpect(jsonPath("$.attendedBy.id").value(user.getId()))
                .andExpect(jsonPath("$.attendedBy.name").value(user.getName()))
                .andExpect(jsonPath("$.details[0].product.id").value(1L))
                .andExpect(jsonPath("$.details[0].quantity").value(2))
                .andExpect(jsonPath("$.details[0].unitPrice").value(10.0));
    }

    @Test
    @WithMockUser
    void shouldConfirmSale() throws Exception {

        Long orderId = 100L;
        String paymentMethod = "Efectivo";

        OrderConfirmSaleRequestDto requestDto = OrderConfirmSaleRequestDto.builder()
                .paymentMethod(paymentMethod)
                .build();

        User user = new User();
        user.setId(10L);
        user.setName("Mateo Velazco");

        OrderConfirmSaleResponseDto responseDto = OrderConfirmSaleResponseDto.builder()
                .id(orderId)
                .date(LocalDateTime.now())
                .clientName("John Doe")
                .status(Order.OrderStatus.PAGADO.name())
                .sale(OrderConfirmSaleResponseDto.SaleOrderConfirmSaleResponseDto.builder()
                        .id(200L)
                        .saleDate(LocalDateTime.now())
                        .paymentMethod(paymentMethod)
                        .totalAmount(BigDecimal.valueOf(100.0))
                        .cashier(
                                OrderConfirmSaleResponseDto.SaleOrderConfirmSaleResponseDto.CashierSaleOrderConfirmSaleResponseDto
                                        .builder()
                                        .id(10L)
                                        .name("Mateo Velazco")
                                        .build())
                        .build())
                .build();

        Mockito.when(orderService.confirmSale(eq(orderId), eq(user), eq(paymentMethod)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/orders/{id}/confirm-sale", orderId)
                .with(csrf())
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PAGADO"))
                .andExpect(jsonPath("$.sale.id").value(200L))
                .andExpect(jsonPath("$.sale.totalAmount").value(100.0));
    }

    @Test
    @WithMockUser
    void shouldDispatchOrderSuccessfully() throws Exception {

        Long orderId = 100L;

        User user = new User();
        user.setId(10L);
        user.setName("Mateo Velazco");

        OrderConfirmDispatchResponseDto responseDto = OrderConfirmDispatchResponseDto.builder()
                .id(orderId)
                .date(LocalDateTime.now())
                .clientName("John Doe")
                .status(Order.OrderStatus.ENTREGADO.name())
                .dispatch(OrderConfirmDispatchResponseDto.DispatchConfirmDispatchResponseDto.builder()
                        .id(user.getId())
                        .deliveryDate(LocalDateTime.now())
                        .dispatchedBy(
                                OrderConfirmDispatchResponseDto.DispatchConfirmDispatchResponseDto.UserDispatchConfirmDispatchResponseDto
                                        .builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .build())
                        .build())
                .build();

        Mockito.when(orderService.confirmDispatch(eq(orderId), eq(user)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/orders/{id}/confirm-dispatch", orderId)
                .with(csrf())
                .with(user(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("ENTREGADO"))
                .andExpect(jsonPath("$.dispatch.id").value(user.getId()))
                .andExpect(jsonPath("$.dispatch.dispatchedBy.id").value(user.getId()))
                .andExpect(jsonPath("$.dispatch.dispatchedBy.name").value(user.getName()));
    }

    @Test
    @WithMockUser
    void shouldCancelOrderSuccessfully() throws Exception {

        Long orderId = 100L;

        Mockito.doNothing().when(orderService).cancelOrder(orderId);

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void shouldGetDeliveredOrdersSuccessfully() throws Exception {

        User attendedByUser = new User();
        attendedByUser.setId(1L);
        attendedByUser.setName("Mateo Velazco");

        User deliveredByUser = new User();
        deliveredByUser.setId(2L);
        deliveredByUser.setName("Camila Sánchez");

        DeliveredOrderResponseDto.AttendedByDto attendedByDto = new DeliveredOrderResponseDto.AttendedByDto(
                attendedByUser.getId(), attendedByUser.getName());

        DeliveredOrderResponseDto.DeliveredByDto deliveredByDto = new DeliveredOrderResponseDto.DeliveredByDto(
                deliveredByUser.getId(), deliveredByUser.getName());

        DeliveredOrderResponseDto.ProductDto productDto = new DeliveredOrderResponseDto.ProductDto(1L, "Producto 1");

        DeliveredOrderResponseDto.OrderDetailDto detailDto = new DeliveredOrderResponseDto.OrderDetailDto(
                2, BigDecimal.valueOf(10.0), productDto);

        DeliveredOrderResponseDto deliveredOrderDto = DeliveredOrderResponseDto.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .clientName("John Doe")
                .status(Order.OrderStatus.ENTREGADO.name())
                .attendedBy(attendedByDto)
                .deliveredBy(deliveredByDto)
                .deliveryDate(LocalDateTime.now())
                .details(List.of(detailDto))
                .build();

        PaginatedResponseDto<DeliveredOrderResponseDto> paginatedResponse = PaginatedResponseDto
                .<DeliveredOrderResponseDto>builder()
                .content(List.of(deliveredOrderDto))
                .currentPage(0)
                .totalItems(1)
                .totalPages(1)
                .build();

        Mockito.when(orderService.getDeliveredOrders(any(Pageable.class)))
                .thenReturn(paginatedResponse);

        mockMvc.perform(get("/api/orders/delivered")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].status").value("ENTREGADO"))
                .andExpect(jsonPath("$.content[0].attendedBy.name").value("Mateo Velazco"))
                .andExpect(jsonPath("$.content[0].deliveredBy.name").value("Camila Sánchez"))
                .andExpect(jsonPath("$.content[0].details[0].product.id").value(1L))
                .andExpect(jsonPath("$.content[0].details[0].quantity").value(2))
                .andExpect(jsonPath("$.content[0].details[0].unitPrice").value(10.0))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

}
