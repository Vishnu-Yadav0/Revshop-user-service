package com.revshop.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revshop.userservice.dto.*;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.service.BuyerService;
import com.revshop.userservice.service.SellerService;
import com.revshop.userservice.service.UserService;
import com.revshop.userservice.service.OtpService;
import com.revshop.userservice.security.JwtUtil;
import com.revshop.userservice.model.User;
import com.revshop.userservice.model.Buyer;
import com.revshop.userservice.model.Seller;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @MockitoBean
    private OtpService otpService;

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

        User mockUser = new User();
        mockUser.setUserId(2L);
        mockUser.setName("Buyer");
        mockUser.setEmail("buyer@example.com");

        Buyer mockBuyer = new Buyer();
        mockBuyer.setUserId(1L);
        mockBuyer.setUser(mockUser);

        Mockito.when(buyerService.registerBuyer(any())).thenReturn(mockBuyer);

        mockMvc.perform(post("/api/auth/register/buyer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterSeller_Success() throws Exception {
        SellerDTO request = new SellerDTO();
        request.setName("Seller");
        request.setEmail("seller@example.com");
        request.setPassword("password");
        request.setBusinessName("My Biz");

        User mockUser = new User();
        mockUser.setUserId(3L);
        mockUser.setName("Seller");
        mockUser.setEmail("seller@example.com");

        Seller mockSeller = new Seller();
        mockSeller.setUserId(1L);
        mockSeller.setBusinessName("My Biz");
        mockSeller.setUser(mockUser);

        Mockito.when(sellerService.registerSeller(any(), any(), any(), any()))
                .thenReturn(mockSeller);

        mockMvc.perform(post("/api/auth/register/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
