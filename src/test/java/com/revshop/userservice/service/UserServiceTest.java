package com.revshop.userservice.service;

import com.revshop.userservice.dto.*;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.UserRepository;
import com.revshop.userservice.repository.PasswordResetTokenRepository;
import com.revshop.userservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setRole(Role.BUYER);
        sampleUser.setActive(true);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setRole(Role.BUYER);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("mockToken");

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("John Doe", response.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("mockToken");

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        UserResponse response = userService.getUserById(1L);
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User updatedData = new User();
        updatedData.setName("John Updated");

        UserResponse response = userService.updateUser(1L, updatedData);
        assertEquals("John Updated", response.getName());
    }

    @Test
    void testUpdatePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userService.updatePassword(1L, "oldPass", "newPass");

        verify(userRepository).save(sampleUser);
        assertEquals("encodedNewPass", sampleUser.getPassword());
    }

    @Test
    void testDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        userService.deactivateUser(1L);
        assertFalse(sampleUser.isActive());
        verify(userRepository).save(sampleUser);
    }
}
