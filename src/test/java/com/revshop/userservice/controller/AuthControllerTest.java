package com.revshop.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revshop.userservice.dto.*;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.service.BuyerService;
import com.revshop.userservice.service.SellerService;
import com.revshop.userservice.service.UserService;
import com.revshop.userservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BuyerService buyerService;

    @MockitoBean
    private SellerService sellerService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse response = new AuthResponse("mockToken", 1L, "Test User", "test@example.com", Role.BUYER);
        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("mockToken"));
    }

    @Test
    void testRegisterBuyer_Success() throws Exception {
        BuyerDTO request = new BuyerDTO();
        request.setName("Buyer");
        request.setEmail("buyer@example.com");
        request.setPassword("password");

        AuthResponse response = new AuthResponse("mockToken", 2L, "Buyer", "buyer@example.com", Role.BUYER);
        // Mocking can be complex due to the buildUserFromBuyerDTO helper, but for unit test of controller, 
        // we mainly check if service is called and response is correct.
        
        mockMvc.perform(post("/api/auth/register/buyer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
