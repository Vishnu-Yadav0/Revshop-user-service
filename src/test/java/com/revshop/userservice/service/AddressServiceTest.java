package com.revshop.userservice.service;

import com.revshop.userservice.dto.AddressDTO;
import com.revshop.userservice.model.Address;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.AddressRepository;
import com.revshop.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User sampleUser;
    private Address sampleAddress;
    private AddressDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1L);

        sampleAddress = new Address();
        sampleAddress.setAddressId(1L);
        sampleAddress.setAddressLine("123 Main St");
        sampleAddress.setUser(sampleUser);

        sampleDTO = new AddressDTO();
        sampleDTO.setUserId(1L);
        sampleDTO.setAddressLine("123 Main St");
    }

    @Test
    void testAddAddress_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);

        AddressDTO result = addressService.addAddress(sampleDTO);

        assertNotNull(result);
        assertEquals("123 Main St", result.getAddressLine());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testGetAddressesByUserId_Success() {
        when(addressRepository.findByUserUserId(1L)).thenReturn(Collections.singletonList(sampleAddress));

        List<AddressDTO> results = addressService.getAddressesByUserId(1L);

        assertEquals(1, results.size());
        assertEquals("123 Main St", results.get(0).getAddressLine());
    }

    @Test
    void testSetDefaultAddress() {
        when(addressRepository.findByUserUserId(1L)).thenReturn(Collections.singletonList(sampleAddress));

        addressService.setDefaultAddress(1L, 1L);

        assertTrue(sampleAddress.getIsDefault());
        verify(addressRepository).save(sampleAddress);
    }
}
