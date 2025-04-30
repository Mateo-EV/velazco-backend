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
import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateActiveRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.mappers.ProductMapper;
import com.velazco.velazco_backend.services.ProductService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        void shouldGetAllProductsSuccessfully() throws Exception {

                Product productEntity = new Product();
                productEntity.setId(1L);
                productEntity.setName("Chocolate Cake");
                productEntity.setPrice(BigDecimal.valueOf(20.00));
                productEntity.setStock(10);
                productEntity.setActive(true);

                Category category = new Category();
                category.setId(2L);
                category.setName("Pasteles");
                productEntity.setCategory(category);

                List<Product> productEntities = List.of(productEntity);

                ProductListResponseDto.CategoryProductListResponseDto categoryDTO = ProductListResponseDto.CategoryProductListResponseDto
                                .builder()
                                .id(2L)
                                .name("Pasteles")
                                .build();

                ProductListResponseDto productDTO = ProductListResponseDto.builder()
                                .id(1)
                                .name("Chocolate Cake")
                                .price(BigDecimal.valueOf(20.00))
                                .stock(10)
                                .active(true)
                                .category(categoryDTO)
                                .build();

                List<ProductListResponseDto> productDTOs = List.of(productDTO);

                Mockito.when(productService.getAllProducts()).thenReturn(productEntities);
                Mockito.when(productMapper.toListResponse(productEntities)).thenReturn(productDTOs);

                mockMvc.perform(get("/api/products")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Chocolate Cake"))
                                .andExpect(jsonPath("$[0].price").value(20.00))
                                .andExpect(jsonPath("$[0].stock").value(10))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[0].category.id").value(2))
                                .andExpect(jsonPath("$[0].category.name").value("Pasteles"));
        }

        @Test
        @WithMockUser
        void shouldCreateProduct() throws Exception {
                ProductCreateRequestDto requestDTO = ProductCreateRequestDto.builder()
                                .name("Chocolate Cake")
                                .price(BigDecimal.valueOf(20.00))
                                .stock(10)
                                .active(true)
                                .categoryId(1L)
                                .build();

                Product mappedEntity = new Product();
                mappedEntity.setId(null);
                mappedEntity.setName("Chocolate Cake");
                mappedEntity.setPrice(BigDecimal.valueOf(20.00));
                mappedEntity.setStock(10);
                mappedEntity.setActive(true);

                Product savedEntity = new Product();
                savedEntity.setId(1L);
                savedEntity.setName("Chocolate Cake");
                savedEntity.setPrice(BigDecimal.valueOf(20.00));
                savedEntity.setStock(10);
                savedEntity.setActive(true);

                Category category = new Category();
                category.setId(1L);
                category.setName("Desserts");
                savedEntity.setCategory(category);

                ProductCreateResponseDto responseDTO = ProductCreateResponseDto.builder()
                                .id(1)
                                .name("Chocolate Cake")
                                .price(BigDecimal.valueOf(20.00))
                                .stock(10)
                                .active(true)
                                .category(ProductCreateResponseDto.CategoryProductCreateResponseDto.builder()
                                                .id(1L)
                                                .name("Desserts")
                                                .build())
                                .build();

                Mockito.when(productMapper.toEntity(any(ProductCreateRequestDto.class))).thenReturn(mappedEntity);
                Mockito.when(productService.createProduct(any(Product.class))).thenReturn(savedEntity);
                Mockito.when(productMapper.toCreateResponse(any(Product.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Chocolate Cake"))
                                .andExpect(jsonPath("$.price").value(20.00))
                                .andExpect(jsonPath("$.stock").value(10))
                                .andExpect(jsonPath("$.active").value(true))
                                .andExpect(jsonPath("$.category.id").value(1L))
                                .andExpect(jsonPath("$.category.name").value("Desserts"));
        }

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
