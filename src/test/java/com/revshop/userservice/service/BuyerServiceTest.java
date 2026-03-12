package com.revshop.userservice.service;

import com.revshop.userservice.model.Buyer;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.BuyerRepository;
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
class BuyerServiceTest {

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BuyerService buyerService;

    private User sampleUser;
    private Buyer sampleBuyer;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Buyer Doe");
        sampleUser.setEmail("buyer@example.com");
        sampleUser.setPassword("password");
        sampleUser.setAddresses(new ArrayList<>());

        sampleBuyer = new Buyer();
        sampleBuyer.setUserId(1L);
        sampleBuyer.setUser(sampleUser);
        sampleUser.setBuyerProfile(sampleBuyer);
    }

    @Test
    void testRegisterBuyer_Success() {
        when(userRepository.existsByEmail("buyer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Buyer result = buyerService.registerBuyer(sampleUser);

        assertNotNull(result);
        assertEquals(Role.BUYER, sampleUser.getRole());
        assertEquals("encodedPassword", sampleUser.getPassword());
        verify(emailService).sendUserRegistrationEmail(eq("buyer@example.com"), anyString());
    }

    @Test
    void testRegisterBuyer_EmailExists() {
        when(userRepository.existsByEmail("buyer@example.com")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> buyerService.registerBuyer(sampleUser));
    }

    @Test
    void testUpdateBuyerProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User updatedData = new User();
        updatedData.setName("New Name");
        updatedData.setPhone("1231231234");

        Buyer result = buyerService.updateBuyerProfile(1L, updatedData);

        assertNotNull(result);
        assertEquals("New Name", sampleUser.getName());
    }
}
