package com.revshop.userservice.service;

import com.revshop.userservice.model.Role;
import com.revshop.userservice.model.Seller;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.SellerRepository;
import com.revshop.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SellerService sellerService;

    private User sampleUser;
    private Seller sampleSeller;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Seller Doe");
        sampleUser.setEmail("seller@example.com");
        sampleUser.setPassword("password");
        sampleUser.setAddresses(new ArrayList<>());

        sampleSeller = new Seller();
        sampleSeller.setUserId(1L);
        sampleSeller.setBusinessName("Test Business");
        sampleSeller.setBusinessDescription("A test business");
        sampleSeller.setTaxId("TAX12345");
        sampleSeller.setUser(sampleUser);
    }

    @Test
    void testRegisterSeller_Success() {
        when(userRepository.existsByEmail("seller@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Seller result = sellerService.registerSeller(sampleUser, "Test Business", "A test business", "TAX12345");

        assertNotNull(result);
        assertEquals(Role.SELLER, sampleUser.getRole());
        assertEquals("encodedPassword", sampleUser.getPassword());
        assertEquals("Test Business", result.getBusinessName());
        verify(emailService).sendUserRegistrationEmail(eq("seller@example.com"), anyString());
    }

    @Test
    void testGetSellerById_Success() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(sampleSeller));
        Optional<Seller> result = sellerService.getSellerById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Business", result.get().getBusinessName());
    }
}
