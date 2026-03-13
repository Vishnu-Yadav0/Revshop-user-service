package com.revshop.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revshop.userservice.dto.PasswordUpdateRequest;
import com.revshop.userservice.dto.UserResponse;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private UserResponse sampleUserResponse;

    @BeforeEach
    void setUp() {
        sampleUserResponse = new UserResponse();
        sampleUserResponse.setUserId(1L);
        sampleUserResponse.setName("John Doe");
        sampleUserResponse.setEmail("john@example.com");
        sampleUserResponse.setRole(Role.BUYER);
        sampleUserResponse.setActive(true);
    }

    @Test
    void testGetAllUsers() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(sampleUserResponse));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("John Doe"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(sampleUserResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }

    @Test
    void testUpdatePassword_Success() throws Exception {
        PasswordUpdateRequest request = new PasswordUpdateRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        mockMvc.perform(put("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));
    }

    @Test
    void testDeactivateAccount() throws Exception {
        mockMvc.perform(put("/api/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deactivated"));
    }
}
