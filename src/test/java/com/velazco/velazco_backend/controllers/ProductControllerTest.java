package com.velazco.velazco_backend.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateActiveRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.mappers.ProductMapper;
import com.velazco.velazco_backend.services.ProductService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductService productService;

  @MockitoBean
  private ProductMapper productMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void shouldUpdateProductActive() throws Exception {
    ProductUpdateActiveRequestDto statusDTO = ProductUpdateActiveRequestDto.builder()
        .active(false)
        .build();

    Product updatedEntity = new Product();
    updatedEntity.setId(1L);
    updatedEntity.setActive(false);

    ProductUpdateActiveResponseDto responseDTO = ProductUpdateActiveResponseDto.builder()
        .id(1L)
        .active(false)
        .build();

    Mockito.when(productService.updateProductActive(eq(1L), eq(false))).thenReturn(updatedEntity);

    Mockito.when(productMapper.toUpdateActiveResponse(updatedEntity)).thenReturn(responseDTO);

    mockMvc.perform(
        patch("/api/products/1/active")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statusDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  @WithMockUser
  void shouldDeleteProduct() throws Exception {
    Mockito.doNothing().when(productService).deleteProductById(1L);

    mockMvc.perform(delete("/api/products/1").with(csrf()))
        .andExpect(status().isNoContent());
  }
}
