package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

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
import com.velazco.velazco_backend.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.entities.Production;
import com.velazco.velazco_backend.entities.ProductionDetail;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.entities.Production.ProductionStatus;
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
}
