package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.velazco.velazco_backend.exception.GlobalExceptionHandler;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.production.request.ProductionFinalizeRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_backend.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_backend.dto.production.response.*;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.entities.Production.ProductionStatus;
import com.velazco.velazco_backend.services.ProductionService;
import com.velazco.velazco_backend.mappers.ProductionMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductionController.class)
@Import(GlobalExceptionHandler.class)
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

    @BeforeEach
    void setup() {
        objectMapper.findAndRegisterModules(); // Para que entienda LocalDate
    }

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
                                        .producedQuantity(15)
                                        .requestedQuantity(15)
                                        .build()))
                        .build());

        Mockito.when(productionService.getCompletedAndIncompleteOrders()).thenReturn(historial);

        mockMvc.perform(get("/api/productions/history").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("OP-0044"))
                .andExpect(jsonPath("$[0].date").value("2023-04-24"))
                .andExpect(jsonPath("$[0].responsible").value("Carlos Gómez"))
                .andExpect(jsonPath("$[0].status").value("COMPLETO"))
                .andExpect(jsonPath("$[0].products[0].productName").value("Pan de Banana"))
                .andExpect(jsonPath("$[0].products[0].producedQuantity").value(15));
    }

    @Test
    @WithMockUser
    void shouldFinalizeProduction() throws Exception {
        Long productionId = 1L;

        // DTO válido
        ProductionFinalizeRequestDto.ProductResultDto productDto = new ProductionFinalizeRequestDto.ProductResultDto();
        productDto.setProductId(1L); // ✔️ requerido
        productDto.setProducedQuantity(80); // ✔️ requerido y válido
        productDto.setMotivoIncompleto("Faltó materia prima");

        ProductionFinalizeRequestDto request = new ProductionFinalizeRequestDto();
        request.setProductos(List.of(productDto)); // ✔️ requerido

        // Respuesta simulada
        ProductionFinalizeResponseDto.ProductResult result = ProductionFinalizeResponseDto.ProductResult.builder()
                .productId(1L)
                .cantidadProducida(80)
                .motivo("Faltó materia prima")
                .build();

        ProductionFinalizeResponseDto response = ProductionFinalizeResponseDto.builder()
                .productionId(productionId)
                .estadoFinal("INCOMPLETO")
                .productos(List.of(result))
                .build();

        // Mock del servicio
        Mockito.when(productionService.finalizeProduction(eq(productionId), eq(request)))
                .thenReturn(response);

        // Ejecución del test
        mockMvc.perform(patch("/api/productions/{id}/finalize", productionId) // ✅ corregido: /finalize
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // te muestra en consola request/response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productionId").value(productionId))
                .andExpect(jsonPath("$.estadoFinal").value("INCOMPLETO"))
                .andExpect(jsonPath("$.productos[0].productId").value(1L))
                .andExpect(jsonPath("$.productos[0].cantidadProducida").value(80))
                .andExpect(jsonPath("$.productos[0].motivo").value("Faltó materia prima"));
    }

    @Test
    @WithMockUser
    void shouldCreateProductionList() throws Exception {
        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setName("administrador");

        ProductionCreateRequestDto request = ProductionCreateRequestDto.builder()
                .productionDate(LocalDate.now())
                .assignedToId(5L)
                .status(ProductionStatus.PENDIENTE)
                .details(List.of(
                        ProductionCreateRequestDto.ProductionDetailCreateRequestDto.builder()
                                .productId(1L)
                                .requestedQuantity(50)
                                .build()))
                .build();

        ProductionCreateResponseDto response = ProductionCreateResponseDto.builder()
                .id(1L)
                .productionDate(request.getProductionDate())
                .status(request.getStatus())
                .assignedBy(ProductionCreateResponseDto.AssignedByProductionCreateResponseDto.builder()
                        .id(mockUser.getId()).name(mockUser.getName()).build())
                .assignedTo(ProductionCreateResponseDto.AssignedToProductionCreateResponseDto.builder()
                        .id(5L).name("Usuario Asignado").build())
                .details(List.of(
                        ProductionCreateResponseDto.DetailProductionCreateResponseDto.builder()
                                .requestedQuantity(50)
                                .producedQuantity(0)
                                .product(ProductionCreateResponseDto.ProductProductionCreateResponseDto.builder()
                                        .id(1L).name("Producto 1").build())
                                .build()))
                .build();

        Mockito.when(productionService.createProduction(eq(request), eq(mockUser))).thenReturn(response);

        mockMvc.perform(post("/api/productions")
                .with(csrf())
                .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.assignedBy.name").value("administrador"))
                .andExpect(jsonPath("$.assignedTo.name").value("Usuario Asignado"))
                .andExpect(jsonPath("$.details[0].requestedQuantity").value(50));
    }

    @Test
    @WithMockUser
    void shouldUpdateProduction() throws Exception {
        Long productionId = 1L;
        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setName("admin");

        ProductionUpdateRequestDto request = ProductionUpdateRequestDto.builder()
                .productionDate(LocalDate.now())
                .assignedToId(3L)
                .status(ProductionStatus.EN_PROCESO)
                .details(List.of(
                        ProductionUpdateRequestDto.ProductionDetailUpdateRequestDto.builder()
                                .productId(1L)
                                .requestedQuantity(100)
                                .build()))
                .build();

        ProductionUpdateResponseDto response = ProductionUpdateResponseDto.builder()
                .id(productionId)
                .productionDate(request.getProductionDate())
                .status(request.getStatus())
                .assignedBy(ProductionUpdateResponseDto.AssignedByProductionUpdateResponseDto.builder()
                        .id(10L).name("admin").build())
                .assignedTo(ProductionUpdateResponseDto.AssignedToProductionUpdateResponseDto.builder()
                        .id(3L).name("Mateo").build())
                .details(List.of(
                        ProductionUpdateResponseDto.DetailProductionUpdateResponseDto.builder()
                                .requestedQuantity(100)
                                .producedQuantity(0)
                                .product(ProductionUpdateResponseDto.ProductProductionUpdateResponseDto.builder()
                                        .id(1L).name("Producto X").build())
                                .build()))
                .build();

        Mockito.when(productionService.updateProduction(eq(productionId), eq(request), eq(mockUser)))
                .thenReturn(response);

        mockMvc.perform(put("/api/productions/{id}", productionId)
                .with(csrf())
                .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productionId))
                .andExpect(jsonPath("$.assignedTo.name").value("Mateo"))
                .andExpect(jsonPath("$.details[0].requestedQuantity").value(100))
                .andExpect(jsonPath("$.details[0].producedQuantity").value(0));
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
        List<ProductionDailyResponseDto> daily = List.of(
                ProductionDailyResponseDto.builder()
                        .id(1L)
                        .productionDate(LocalDate.now())
                        .status(ProductionStatus.PENDIENTE)
                        .assignedBy(ProductionDailyResponseDto.AssignedByDto.builder().id(1L).name("Admin").build())
                        .assignedTo(ProductionDailyResponseDto.AssignedToDto.builder().id(2L).name("Mateo").build())
                        .details(List.of(
                                ProductionDailyResponseDto.DetailDto.builder()
                                        .product(ProductionDailyResponseDto.ProductDto.builder()
                                                .id(1L)
                                                .name("Producto A")
                                                .build())
                                        .requestedQuantity(50)
                                        .producedQuantity(10)
                                        .build()))
                        .build());

        Mockito.when(productionService.getDailyProductions()).thenReturn(daily);

        mockMvc.perform(get("/api/productions/daily")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].assignedBy.name").value("Admin"))
                .andExpect(jsonPath("$[0].assignedTo.name").value("Mateo"))
                .andExpect(jsonPath("$[0].details[0].product.name").value("Producto A"))
                .andExpect(jsonPath("$[0].details[0].requestedQuantity").value(50))
                .andExpect(jsonPath("$[0].details[0].producedQuantity").value(10));
    }

}
