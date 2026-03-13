package com.revshop.userservice.controller;

import com.revshop.userservice.dto.AddressDTO;
import com.revshop.userservice.dto.ApiResponse;
import com.revshop.userservice.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> addAddress(@RequestBody AddressDTO addressDTO) {
        log.info("POST /api/addresses - userId={}", addressDTO.getUserId());
        AddressDTO created = addressService.addAddress(addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Address added successfully", created));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddressesByUserId(@PathVariable Long userId) {
        log.info("GET /api/addresses/user/{}", userId);
        List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>("Addresses fetched", addresses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(@PathVariable Long id) {
        log.info("GET /api/addresses/{}", id);
        return ResponseEntity.ok(new ApiResponse<>("Address fetched", addressService.getAddressById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        log.info("PUT /api/addresses/{}", id);
        AddressDTO updated = addressService.updateAddress(id, addressDTO);
        return ResponseEntity.ok(new ApiResponse<>("Address updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long id) {
        log.info("DELETE /api/addresses/{}", id);
        addressService.deleteAddress(id);
        return ResponseEntity.ok(new ApiResponse<>("Address deleted successfully", null));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<String>> setDefaultAddress(@PathVariable Long id, @RequestParam Long userId) {
        log.info("PUT /api/addresses/{}/default - userId={}", id, userId);
        addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(new ApiResponse<>("Default address set", null));
    }
}
