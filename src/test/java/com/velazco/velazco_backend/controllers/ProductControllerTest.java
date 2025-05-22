package com.velazco.velazco_backend.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.product.requests.ProductCreateRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateActiveRequestDto;
import com.velazco.velazco_backend.dto.product.requests.ProductUpdateRequestDto;
import com.velazco.velazco_backend.dto.product.responses.ProductCreateResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateActiveResponseDto;
import com.velazco.velazco_backend.dto.product.responses.ProductUpdateResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.entities.Product;
import com.velazco.velazco_backend.mappers.ProductMapper;
import com.velazco.velazco_backend.services.ProductService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
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
  void shouldGetAllAvailableProducts() throws Exception {

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

    Mockito.when(productService.getAllAvailableProducts()).thenReturn(productDTOs);
    Mockito.when(productMapper.toListResponse(productEntities)).thenReturn(productDTOs);

    mockMvc.perform(get("/api/products/available")
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
    Path imagePath = Paths.get("uploads/test.jpg");
    byte[] imageBytes = Files.readAllBytes(imagePath);

    MockMultipartFile image = new MockMultipartFile(
        "image",
        "test.jpg",
        "image/jpeg",
        imageBytes);

    ProductCreateRequestDto requestDTO = ProductCreateRequestDto.builder()
        .name("Chocolate Cake")
        .price(BigDecimal.valueOf(20.00))
        .stock(10)
        .active(true)
        .categoryId(1L)
        .image(image)
        .build();

    Category category = new Category();
    category.setId(1L);
    category.setName("Desserts");

    Product mappedEntity = new Product();
    mappedEntity.setName(requestDTO.getName());
    mappedEntity.setPrice(requestDTO.getPrice());
    mappedEntity.setStock(requestDTO.getStock());
    mappedEntity.setActive(requestDTO.getActive());
    mappedEntity.setCategory(category);
    mappedEntity.setImage("/images/test.jpg");

    Product savedEntity = new Product();
    savedEntity.setId(1L);
    savedEntity.setName("Chocolate Cake");
    savedEntity.setPrice(BigDecimal.valueOf(20.00));
    savedEntity.setStock(10);
    savedEntity.setActive(true);
    savedEntity.setImage("/images/test.jpg");
    savedEntity.setCategory(category);

    ProductCreateResponseDto responseDTO = ProductCreateResponseDto.builder()
        .id(1L)
        .name("Chocolate Cake")
        .price(BigDecimal.valueOf(20.00))
        .stock(10)
        .active(true)
        .image("/images/test.jpg")
        .category(ProductCreateResponseDto.CategoryProductCreateResponseDto.builder()
            .id(1L)
            .name("Desserts")
            .build())
        .build();

    Mockito.when(productMapper.toEntity(any(ProductCreateRequestDto.class))).thenReturn(mappedEntity);
    Mockito.when(productService.createProductWithImage(any(ProductCreateRequestDto.class)))
        .thenReturn(responseDTO);
    Mockito.when(productMapper.toCreateResponse(any(Product.class))).thenReturn(responseDTO);

    mockMvc.perform(multipart("/api/products")
        .file(image)
        .with(csrf())
        .param("name", requestDTO.getName())
        .param("price", requestDTO.getPrice().toString())
        .param("stock", requestDTO.getStock().toString())
        .param("active", requestDTO.getActive().toString())
        .param("categoryId", requestDTO.getCategoryId().toString()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Chocolate Cake"))
        .andExpect(jsonPath("$.price").value(20.00))
        .andExpect(jsonPath("$.stock").value(10))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.image").value("/images/test.jpg"))
        .andExpect(jsonPath("$.category.id").value(1L))
        .andExpect(jsonPath("$.category.name").value("Desserts"));
  }

  @Test
  @WithMockUser
  void shouldUpdateProduct() throws Exception {
    Path imagePath = Paths.get("uploads/test.jpg");
    byte[] imageBytes = Files.readAllBytes(imagePath);

    MockMultipartFile image = new MockMultipartFile(
        "image",
        "test.jpg",
        "image/jpeg",
        imageBytes);

    ProductUpdateRequestDto requestDTO = ProductUpdateRequestDto.builder()
        .name("Updated Cake")
        .price(BigDecimal.valueOf(25))
        .stock(5)
        .active(true)
        .categoryId(1L)
        .image(image)
        .build();

    Category category = new Category();
    category.setId(1L);
    category.setName("Desserts");

    Product mappedEntity = new Product();
    mappedEntity.setId(1L);
    mappedEntity.setName("Updated Cake");
    mappedEntity.setPrice(BigDecimal.valueOf(25));
    mappedEntity.setStock(5);
    mappedEntity.setActive(true);
    mappedEntity.setCategory(category);
    mappedEntity.setImage("/images/test.jpg");

    Product updatedEntity = new Product();
    updatedEntity.setId(1L);
    updatedEntity.setName("Updated Cake");
    updatedEntity.setPrice(BigDecimal.valueOf(25));
    updatedEntity.setStock(5);
    updatedEntity.setActive(true);
    updatedEntity.setImage("/images/test.jpg");
    updatedEntity.setCategory(category);

    ProductUpdateResponseDto responseDTO = ProductUpdateResponseDto.builder()
        .id(1L)
        .name("Updated Cake")
        .price(BigDecimal.valueOf(25))
        .stock(5)
        .active(true)
        .image("/images/test.jpg")
        .category(ProductUpdateResponseDto.CategoryProductUpdateResponseDto.builder()
            .id(1L)
            .name("Desserts")
            .build())
        .build();

    Mockito.when(productMapper.toEntity(any(ProductUpdateRequestDto.class))).thenReturn(mappedEntity);
    Mockito.when(productService.updateProduct(eq(1L), any(ProductUpdateRequestDto.class)))
        .thenReturn(updatedEntity);
    Mockito.when(productMapper.toUpdateResponse(updatedEntity)).thenReturn(responseDTO);

    mockMvc.perform(multipart("/api/products/1")
        .file(image)
        .with(csrf())
        .with(request -> {
          request.setMethod("PUT");
          return request;
        })
        .param("name", requestDTO.getName())
        .param("price", requestDTO.getPrice().toString())
        .param("stock", requestDTO.getStock().toString())
        .param("active", requestDTO.getActive().toString())
        .param("categoryId", requestDTO.getCategoryId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Updated Cake"))
        .andExpect(jsonPath("$.price").value(25.00))
        .andExpect(jsonPath("$.stock").value(5))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.image").value("/images/test.jpg"))
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
