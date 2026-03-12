package com.revshop.userservice.service;

import com.revshop.userservice.model.Address;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.AddressRepository;
import com.revshop.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressService {
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public void setDefaultAddress(Long addressId, Long userId) {
        log.info("Setting default address id={} for userId={}", addressId, userId);
        List<Address> addresses = addressRepository.findByUserUserId(userId);
        addresses.forEach(addr -> {
            addr.setIsDefault(addr.getAddressId().equals(addressId));
            addressRepository.save(addr);
        });
    }

    public com.revshop.userservice.dto.AddressDTO addAddress(com.revshop.userservice.dto.AddressDTO dto) {
        log.info("Adding address for userId={}", dto.getUserId());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = new Address();
        address.setAddressLine(dto.getAddressLine());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        address.setAddressType(dto.getAddressType());
        address.setIsDefault(dto.getIsDefault());
        address.setUser(user);
        
        Address saved = addressRepository.save(address);
        return mapToDTO(saved);
    }

    public com.revshop.userservice.dto.AddressDTO updateAddress(Long id, com.revshop.userservice.dto.AddressDTO dto) {
        log.info("Updating address id={}", id);
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        existing.setAddressLine(dto.getAddressLine());
        existing.setStreet(dto.getStreet());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setZipCode(dto.getZipCode());
        existing.setCountry(dto.getCountry());
        existing.setAddressType(dto.getAddressType());
        existing.setIsDefault(dto.getIsDefault());

        Address saved = addressRepository.save(existing);
        return mapToDTO(saved);
    }

    public List<com.revshop.userservice.dto.AddressDTO> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private com.revshop.userservice.dto.AddressDTO mapToDTO(Address addr) {
        com.revshop.userservice.dto.AddressDTO d = new com.revshop.userservice.dto.AddressDTO();
        d.setAddressId(addr.getAddressId());
        d.setUserId(addr.getUser().getUserId());
        d.setAddressLine(addr.getAddressLine());
        d.setStreet(addr.getStreet());
        d.setCity(addr.getCity());
        d.setState(addr.getState());
        d.setZipCode(addr.getZipCode());
        d.setCountry(addr.getCountry());
        d.setAddressType(addr.getAddressType());
        d.setIsDefault(addr.getIsDefault());
        return d;
    }

    public void deleteAddress(Long addressId) {
        log.info("Deleting address id={}", addressId);
        if (!addressRepository.existsById(addressId)) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteById(addressId);
    }
}
