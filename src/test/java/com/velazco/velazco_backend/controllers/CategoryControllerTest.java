package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velazco.velazco_backend.dto.category.requests.CategoryCreateRequestDto;
import com.velazco.velazco_backend.dto.category.requests.CategoryUpdateRequestDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryCreateResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_backend.dto.category.responses.CategoryUpdateResponseDto;
import com.velazco.velazco_backend.entities.Category;
import com.velazco.velazco_backend.mappers.CategoryMapper;
import com.velazco.velazco_backend.services.CategoryService;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldGetAllCategoriesSuccessfully() throws Exception {

        Category categoryEntity = new Category();
        categoryEntity.setId(1L);
        categoryEntity.setName("Pasteles");

        List<Category> categoryEntities = List.of(categoryEntity);

        CategoryListResponseDto categoryDTO = CategoryListResponseDto
                .builder()
                .id(1L)
                .name("Pasteles")
                .build();

        List<CategoryListResponseDto> categoriesDTOs = List.of(categoryDTO);

        Mockito.when(categoryService.getAllCategories()).thenReturn(categoryEntities);
        Mockito.when(categoryMapper.toListResponse(categoryEntities)).thenReturn(categoriesDTOs);

        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pasteles"));
    }

    @Test
    @WithMockUser
    void shouldCreateCategory() throws Exception {
        CategoryCreateRequestDto requestDTO = CategoryCreateRequestDto.builder()
                .name("Cakes")
                .build();

        Category mappedEntity = new Category();
        mappedEntity.setId(null);
        mappedEntity.setName("Cakes");

        Category savedEntity = new Category();
        savedEntity.setId(1L);
        savedEntity.setName("Cakes");

        CategoryCreateResponseDto responseDTO = CategoryCreateResponseDto.builder()
                .id(1L)
                .name("Cakes")
                .build();

        Mockito.when(categoryMapper.toEntity(any(CategoryCreateRequestDto.class))).thenReturn(mappedEntity);
        Mockito.when(categoryService.createCategory(any(Category.class))).thenReturn(savedEntity);
        Mockito.when(categoryMapper.toCreateResponse(any(Category.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cakes"));
    }

    @Test
    @WithMockUser
    void shouldUpdateCategory() throws Exception {
        CategoryUpdateRequestDto requestDTO = CategoryUpdateRequestDto.builder()
                .name("Cakes")
                .build();

        Category mappedEntity = new Category();
        mappedEntity.setId(1L);
        mappedEntity.setName("Cakes");

        Category updatedEntity = new Category();
        updatedEntity.setId(1L);
        updatedEntity.setName("Cakes");

        CategoryUpdateResponseDto responseDTO = CategoryUpdateResponseDto.builder()
                .id(1L)
                .name("Cakes")
                .build();

        Mockito.when(categoryMapper.toEntity(any(CategoryUpdateRequestDto.class))).thenReturn(mappedEntity);
        Mockito.when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedEntity);
        Mockito.when(categoryMapper.toUpdateResponse(updatedEntity)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/categories/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cakes"));
    }

    @Test
    @WithMockUser
    void shouldDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategoryById(1L);

        mockMvc.perform(delete("/api/categories/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

}
