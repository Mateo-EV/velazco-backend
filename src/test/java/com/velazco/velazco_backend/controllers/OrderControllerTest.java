package com.velazco.velazco_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.PaginatedResponseDto;
import com.velazco.velazco_backend.dto.order.requests.OrderConfirmSaleRequestDto;
import com.velazco.velazco_backend.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_backend.dto.order.responses.DailySaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.DeliveredOrderResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_backend.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_backend.dto.order.responses.PaymentMethodSummaryDto;
import com.velazco.velazco_backend.dto.order.responses.TopProductDto;
import com.velazco.velazco_backend.dto.order.responses.WeeklySaleResponseDto;
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
import java.time.LocalDate;
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

    OrderStartRequestDto requestDto = OrderStartRequestDto.builder()
        .clientName("John Doe")
        .details(List.of(
            new OrderStartRequestDto.DetailOrderStartRequestDto(1L, 2)))
        .build();

    User user = new User();
    user.setId(10L);
    user.setName("Mateo Velazco");

    Product mockProduct = new Product();
    mockProduct.setId(1L);
    mockProduct.setName("Producto 1");
    mockProduct.setPrice(BigDecimal.valueOf(10.0));

    OrderDetail detail = new OrderDetail();
    detail.setProduct(mockProduct);
    detail.setQuantity(2);

    Order orderEntity = new Order();
    orderEntity.setDetails(List.of(detail));

    Mockito.when(orderMapper.toEntity(eq(requestDto))).thenReturn(orderEntity);
    Mockito.when(productRepository.findAllById(List.of(1L)))
        .thenReturn(List.of(mockProduct));
    Mockito.when(productRepository.decrementStock(1L, 2))
        .thenReturn(1);

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

  @Test
  @WithMockUser
  void shouldGetDailySalesDetailedSuccessfully() throws Exception {

    DailySaleResponseDto.ProductSold productSold1 = DailySaleResponseDto.ProductSold.builder()
        .productName("Pan francés")
        .quantitySold(10)
        .unitPrice(BigDecimal.valueOf(1.50))
        .subtotal(BigDecimal.valueOf(15.00))
        .build();

    DailySaleResponseDto.ProductSold productSold2 = DailySaleResponseDto.ProductSold.builder()
        .productName("Pastel de chocolate")
        .quantitySold(2)
        .unitPrice(BigDecimal.valueOf(25.00))
        .subtotal(BigDecimal.valueOf(50.00))
        .build();

    DailySaleResponseDto dailySale = DailySaleResponseDto.builder()
        .date(LocalDate.now())
        .totalSales(BigDecimal.valueOf(65.00))
        .salesCount(4) // <- Asegúrate de incluir este nuevo campo
        .products(List.of(productSold1, productSold2))
        .build();

    Mockito.when(orderService.getDailySalesDetailed())
        .thenReturn(List.of(dailySale));

    mockMvc.perform(get("/api/orders/daily-sales/details"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].date").value(LocalDate.now().toString()))
        .andExpect(jsonPath("$[0].totalSales").value(65.00))
        .andExpect(jsonPath("$[0].salesCount").value(4)) // <- Nuevo assert
        .andExpect(jsonPath("$[0].products[0].productName").value("Pan francés"))
        .andExpect(jsonPath("$[0].products[0].quantitySold").value(10))
        .andExpect(jsonPath("$[0].products[0].unitPrice").value(1.50))
        .andExpect(jsonPath("$[0].products[0].subtotal").value(15.00))
        .andExpect(jsonPath("$[0].products[1].productName").value("Pastel de chocolate"))
        .andExpect(jsonPath("$[0].products[1].quantitySold").value(2))
        .andExpect(jsonPath("$[0].products[1].unitPrice").value(25.00))
        .andExpect(jsonPath("$[0].products[1].subtotal").value(50.00));
  }

  @Test
  @WithMockUser
  void shouldReturnWeeklySalesDetailedSuccessfully() throws Exception {
    // === Mock data ===
    WeeklySaleResponseDto.ProductSold product1 = WeeklySaleResponseDto.ProductSold.builder()
        .productName("Pan francés")
        .quantitySold(2)
        .unitPrice(BigDecimal.valueOf(2.0))
        .subtotal(BigDecimal.valueOf(4.0))
        .build();

    WeeklySaleResponseDto.ProductSold product2 = WeeklySaleResponseDto.ProductSold.builder()
        .productName("Torta de chocolate")
        .quantitySold(1)
        .unitPrice(BigDecimal.valueOf(20.0))
        .subtotal(BigDecimal.valueOf(20.0))
        .build();

    WeeklySaleResponseDto.DeliveredOrder order1 = WeeklySaleResponseDto.DeliveredOrder.builder()
        .orderId(1L)
        .deliveryDate(LocalDate.of(2025, 7, 8))
        .dayOfWeek("TUESDAY")
        .orderTotal(BigDecimal.valueOf(24.0))
        .products(List.of(product1, product2))
        .build();

    WeeklySaleResponseDto response = WeeklySaleResponseDto.builder()
        .startDate(LocalDate.of(2025, 7, 8))
        .endDate(LocalDate.of(2025, 7, 14))
        .totalSales(BigDecimal.valueOf(24.0))
        .salesCount(1)
        .orders(List.of(order1))
        .build();

    // === Mocking service ===
    Mockito.when(orderService.getWeeklySalesDetailed()).thenReturn(List.of(response));

    // === Perform request ===
    mockMvc.perform(get("/api/orders/weekly-sales/details"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].startDate").value("2025-07-08"))
        .andExpect(jsonPath("$[0].endDate").value("2025-07-14"))
        .andExpect(jsonPath("$[0].totalSales").value(24.0))
        .andExpect(jsonPath("$[0].salesCount").value(1))
        .andExpect(jsonPath("$[0].orders[0].orderId").value(1))
        .andExpect(jsonPath("$[0].orders[0].dayOfWeek").value("TUESDAY"))
        .andExpect(jsonPath("$[0].orders[0].products[0].productName").value("Pan francés"))
        .andExpect(jsonPath("$[0].orders[0].products[1].productName").value("Torta de chocolate"));
  }

  @Test
  @WithMockUser
  void shouldReturnTopSellingProductsOfMonth() throws Exception {
    TopProductDto topProduct1 = TopProductDto.builder()
        .productName("Pan francés")
        .totalQuantitySold(100)
        .totalRevenue(BigDecimal.valueOf(150.00))
        .build();

    TopProductDto topProduct2 = TopProductDto.builder()
        .productName("Pastel de chocolate")
        .totalQuantitySold(50)
        .totalRevenue(BigDecimal.valueOf(1250.00))
        .build();

    Mockito.when(orderService.getTopSellingProductsOfCurrentMonth())
        .thenReturn(List.of(topProduct1, topProduct2));

    mockMvc.perform(get("/api/orders/top-products/month"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].productName").value("Pan francés"))
        .andExpect(jsonPath("$[0].totalQuantitySold").value(100))
        .andExpect(jsonPath("$[0].totalRevenue").value(150.00))
        .andExpect(jsonPath("$[1].productName").value("Pastel de chocolate"))
        .andExpect(jsonPath("$[1].totalQuantitySold").value(50))
        .andExpect(jsonPath("$[1].totalRevenue").value(1250.00));
  }

  @Test
  @WithMockUser
  void shouldReturnSalesByPaymentMethodSummary() throws Exception {

    PaymentMethodSummaryDto efectivo = PaymentMethodSummaryDto.builder()
        .paymentMethod("EFECTIVO")
        .totalSales(BigDecimal.valueOf(1500.00))
        .percentage(60.0)
        .build();

    PaymentMethodSummaryDto tarjeta = PaymentMethodSummaryDto.builder()
        .paymentMethod("TARJETA")
        .totalSales(BigDecimal.valueOf(800.00))
        .percentage(32.0)
        .build();

    PaymentMethodSummaryDto transferencia = PaymentMethodSummaryDto.builder()
        .paymentMethod("TRANSFERENCIA")
        .totalSales(BigDecimal.valueOf(200.00))
        .percentage(8.0)
        .build();

    List<PaymentMethodSummaryDto> response = List.of(efectivo, tarjeta, transferencia);

    Mockito.when(orderService.getSalesByPaymentMethod()).thenReturn(response);

    mockMvc.perform(get("/api/orders/payment-methods/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].paymentMethod").value("EFECTIVO"))
        .andExpect(jsonPath("$[0].totalSales").value(1500.00))
        .andExpect(jsonPath("$[0].percentage").value(60.0))
        .andExpect(jsonPath("$[1].paymentMethod").value("TARJETA"))
        .andExpect(jsonPath("$[1].totalSales").value(800.00))
        .andExpect(jsonPath("$[1].percentage").value(32.0))
        .andExpect(jsonPath("$[2].paymentMethod").value("TRANSFERENCIA"))
        .andExpect(jsonPath("$[2].totalSales").value(200.00))
        .andExpect(jsonPath("$[2].percentage").value(8.0));
  }

}
