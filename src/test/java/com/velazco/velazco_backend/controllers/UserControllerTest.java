package com.velazco.velazco_backend.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.velazco.velazco_backend.dto.user.request.UserCreateRequestDto;
import com.velazco.velazco_backend.dto.user.response.UserCreateResponseDto;
import com.velazco.velazco_backend.mappers.UserMapper;
import com.velazco.velazco_backend.services.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserMapper userMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void shouldCreateUser() throws Exception {
    UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
        .name("Test User")
        .email("test@example.com")
        .password("password123")
        .active(true)
        .roleId(1L)
        .build();

    UserCreateResponseDto.RoleUserCreateResponseDto roleDto = UserCreateResponseDto.RoleUserCreateResponseDto.builder()
        .id(1L)
        .name("Admin")
        .build();

    UserCreateResponseDto responseDto = UserCreateResponseDto.builder()
        .id(1L)
        .name("Test User")
        .email("test@example.com")
        .active(true)
        .role(roleDto)
        .build();

    Mockito.when(userService.createUser(eq(requestDto))).thenReturn(responseDto);

    mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto))
        .with(csrf()))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Test User"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.role.id").value(1L))
        .andExpect(jsonPath("$.role.name").value("Admin"));
  }
}
