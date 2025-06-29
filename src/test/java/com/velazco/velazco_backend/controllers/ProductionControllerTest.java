package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionListResponseDto;
import com.velazco.velazco_backend.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Production;
import com.velazco.velazco_backend.entities.Production.ProductionStatus;
import com.velazco.velazco_backend.entities.ProductionDetail;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.ProductionMapper;
import com.velazco.velazco_backend.services.ProductionService;

@WebMvcTest(ProductionController.class)
@ActiveProfiles("test")
public class ProductionControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductionService productionService;

  @MockitoBean
  private ProductionMapper productionMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void shouldGetCompletedAndIncompleteProductions() throws Exception {
    List<ProductionHistoryResponseDto> historial = List.of(
        ProductionHistoryResponseDto.builder()
            .orderNumber("OP-0044")
            .date(LocalDate.of(2023, 4, 24))
            .responsible("Carlos Gómez")
            .status("COMPLETO")
            .products(List.of(
                ProductionHistoryResponseDto.ProductDetail.builder()
                    .productName("Pan de Banana")
                    .quantity(15)
                    .build(),
                ProductionHistoryResponseDto.ProductDetail.builder()
                    .productName("Alfajores")
                    .quantity(40)
                    .build()))
            .build());

    Mockito.when(productionService.getCompletedAndIncompleteOrders()).thenReturn(historial);

    mockMvc.perform(get("/api/productions/historial")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderNumber").value("OP-0044"))
        .andExpect(jsonPath("$[0].date").value("2023-04-24"))
        .andExpect(jsonPath("$[0].responsible").value("Carlos Gómez"))
        .andExpect(jsonPath("$[0].status").value("COMPLETO"))
        .andExpect(jsonPath("$[0].products[0].productName").value("Pan de Banana"))
        .andExpect(jsonPath("$[0].products[0].quantity").value(15))
        .andExpect(jsonPath("$[0].products[1].productName").value("Alfajores"))
        .andExpect(jsonPath("$[0].products[1].quantity").value(40));
  }

  @Test
  @WithMockUser
  void shouldCreateProductionList() throws Exception {
    User mockUser = new User();
    mockUser.setId(10L);
    mockUser.setName("administrador");

    User assignedUser = new User();
    assignedUser.setId(5L);
    assignedUser.setName("asignado");

    ProductionCreateRequestDto request = ProductionCreateRequestDto.builder()
        .productionDate(LocalDate.now())
        .assignedToId(assignedUser.getId())
        .status(ProductionStatus.PENDIENTE)
        .details(List.of(
            ProductionCreateRequestDto.ProductionDetailCreateRequestDto.builder()
                .productId(1L)
                .requestedQuantity(100)
                .comments("Urgent")
                .build()))
        .build();

    Production mappedEntity = Production.builder()
        .productionDate(request.getProductionDate())
        .assignedTo(assignedUser)
        .status(request.getStatus())
        .details(List.of(
            ProductionDetail.builder()
                .product(Product.builder().id(1L).build())
                .requestedQuantity(100)
                .comments("Urgent")
                .build()))
        .build();

    ProductionCreateResponseDto productionResponse = ProductionCreateResponseDto.builder()
        .id(1L)
        .productionDate(mappedEntity.getProductionDate())
        .status(mappedEntity.getStatus())
        .assignedBy(ProductionCreateResponseDto.AssignedByProductionCreateResponseDto.builder()
            .id(mockUser.getId())
            .name(mockUser.getName())
            .build())
        .assignedTo(ProductionCreateResponseDto.AssignedToProductionCreateResponseDto.builder()
            .id(assignedUser.getId())
            .name(assignedUser.getName())
            .build())
        .details(List.of(
            ProductionCreateResponseDto.DetailProductionCreateResponseDto.builder()
                .requestedQuantity(100)
                .comments("Urgent")
                .producedQuantity(0)
                .product(ProductionCreateResponseDto.ProductProductionCreateResponseDto.builder()
                    .id(1L)
                    .name("Product 1")
                    .build())
                .build()))
        .build();

    Mockito.when(productionMapper.toEntity(eq(request))).thenReturn(mappedEntity);
    Mockito.when(productionService.createProduction(eq(request), eq(mockUser))).thenReturn(productionResponse);

    mockMvc.perform(post("/api/productions").with(csrf()).with(user(mockUser))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.productionDate").value(request.getProductionDate().toString()))
        .andExpect(jsonPath("$.status").value(request.getStatus().name()))
        .andExpect(jsonPath("$.assignedBy.id").value(mockUser.getId()))
        .andExpect(jsonPath("$.assignedBy.name").value(mockUser.getName()))
        .andExpect(jsonPath("$.assignedTo.id").value(assignedUser.getId()))
        .andExpect(jsonPath("$.assignedTo.name").value(assignedUser.getName()))
        .andExpect(jsonPath("$.details[0].requestedQuantity").value(100))
        .andExpect(jsonPath("$.details[0].comments").value("Urgent"))
        .andExpect(jsonPath("$.details[0].producedQuantity").value(0))
        .andExpect(jsonPath("$.details[0].product.id").value(1L))
        .andExpect(jsonPath("$.details[0].product.name").value("Product 1"));

  }

  @Test
  @WithMockUser
  void shouldUpdateProduction() throws Exception {
    Long productionId = 1L;

    User mockUser = new User();
    mockUser.setId(10L);
    mockUser.setName("administrador");

    ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto detailDtoRequest = ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto
        .builder()
        .productId(27L)
        .requestedQuantity(100)
        .comments("Aumentar la producción del producto 27")
        .build();

    ProductionUpdateRequestDto requestDto = ProductionUpdateRequestDto.builder()
        .productionDate(LocalDate.now())
        .assignedToId(1L)
        .status(ProductionStatus.EN_PROCESO)
        .details(List.of(detailDtoRequest))
        .build();

    ProductionUpdateResponseDto productionResponse = ProductionUpdateResponseDto.builder()
        .id(productionId)
        .productionDate(requestDto.getProductionDate())
        .status(requestDto.getStatus())
        .assignedBy(ProductionUpdateResponseDto.AssignedByProductionUpdateResponseDto.builder()
            .id(mockUser.getId())
            .name(mockUser.getName())
            .build())
        .assignedTo(ProductionUpdateResponseDto.AssignedToProductionUpdateResponseDto.builder()
            .id(1L)
            .name("Mateo")
            .build())
        .details(List.of(
            ProductionUpdateResponseDto.DetailProductionUpdateResponseDto.builder()
                .product(ProductionUpdateResponseDto.ProductProductionUpdateResponseDto.builder()
                    .id(27L)
                    .name("Producto A")
                    .build())
                .requestedQuantity(100)
                .producedQuantity(0)
                .comments("Aumentar la producción del producto 27")
                .build()))
        .build();

    Mockito.when(productionService.updateProduction(eq(productionId), eq(requestDto), eq(mockUser)))
        .thenReturn(productionResponse);

    mockMvc.perform(put("/api/productions/{id}", productionId)
        .with(csrf())
        .with(user(mockUser))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productionId))
        .andExpect(jsonPath("$.productionDate").value(requestDto.getProductionDate().toString()))
        .andExpect(jsonPath("$.status").value(requestDto.getStatus().name()))
        .andExpect(jsonPath("$.assignedBy.id").value(mockUser.getId()))
        .andExpect(jsonPath("$.assignedBy.name").value(mockUser.getName()))
        .andExpect(jsonPath("$.assignedTo.id").value(1L))
        .andExpect(jsonPath("$.assignedTo.name").value("Mateo"))
        .andExpect(jsonPath("$.details[0].product.id").value(27L))
        .andExpect(jsonPath("$.details[0].product.name").value("Producto A"))
        .andExpect(jsonPath("$.details[0].requestedQuantity").value(100))
        .andExpect(jsonPath("$.details[0].producedQuantity").value(0))
        .andExpect(jsonPath("$.details[0].comments").value("Aumentar la producción del producto 27"));
  }

  @Test
  @WithMockUser
  void shouldDeleteProduction() throws Exception {
    Mockito.doNothing().when(productionService).deleteProductionById(1L);

    mockMvc.perform(delete("/api/productions/1").with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  void shouldGetDailyProductions() throws Exception {
    List<ProductionListResponseDto> dailyProductions = List.of(
        ProductionListResponseDto.builder()
            .id(1L)
            .productionDate(LocalDate.now())
            .status(ProductionStatus.PENDIENTE)
            .assignedBy(ProductionListResponseDto.AssignedByProductionListResponseDto.builder()
                .id(10L)
                .name("administrador")
                .build())
            .assignedTo(ProductionListResponseDto.AssignedToProductionListResponseDto.builder()
                .id(1L)
                .name("Mateo")
                .build())
            .details(List.of(
                ProductionListResponseDto.DetailProductionListResponseDto.builder()
                    .product(ProductionListResponseDto.ProductProductionListResponseDto.builder()
                        .id(27L)
                        .name("Producto A")
                        .build())
                    .requestedQuantity(100)
                    .producedQuantity(0)
                    .comments("Aumentar la producción del producto 27")
                    .build()))
            .build());

    Mockito.when(productionService.getDailyProductions()).thenReturn(dailyProductions);

    mockMvc.perform(get("/api/productions/daily").with(csrf())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].productionDate").value(LocalDate.now().toString()))
        .andExpect(jsonPath("$[0].status").value(ProductionStatus.PENDIENTE.name()))
        .andExpect(jsonPath("$[0].assignedBy.id").value(10L))
        .andExpect(jsonPath("$[0].assignedBy.name").value("administrador"))
        .andExpect(jsonPath("$[0].assignedTo.id").value(1L))
        .andExpect(jsonPath("$[0].assignedTo.name").value("Mateo"))
        .andExpect(jsonPath("$[0].details[0].product.id").value(27L))
        .andExpect(jsonPath("$[0].details[0].product.name").value("Producto A"))
        .andExpect(jsonPath("$[0].details[0].requestedQuantity").value(100))
        .andExpect(jsonPath("$[0].details[0].producedQuantity").value(0))
        .andExpect(jsonPath("$[0].details[0].comments").value("Aumentar la producción del producto 27"));
  }
}
