package com.revshop.userservice.service;

import com.revshop.userservice.model.Seller;
import com.revshop.userservice.model.User;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.repository.SellerRepository;
import com.revshop.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class SellerService {
    private static final Logger log = LoggerFactory.getLogger(SellerService.class);
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public SellerService(SellerRepository sellerRepository,
                         UserRepository userRepository,
                         EmailService emailService,
                         org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Seller registerSeller(User user, String businessName, String businessDescription, String taxId) {
        log.info("Registering seller email={}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        user.setRole(Role.SELLER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Seller sellerProfile = new Seller();
        sellerProfile.setBusinessName(businessName);
        sellerProfile.setBusinessDescription(businessDescription);
        sellerProfile.setTaxId(taxId);
        sellerProfile.setUser(user);
        user.setSellerProfile(sellerProfile);

        if (user.getAddresses() != null) {
            user.getAddresses().forEach(address -> address.setUser(user));
        }

        User savedUser = userRepository.save(user);
        emailService.sendUserRegistrationEmail(savedUser.getEmail(), savedUser.getName());
        return savedUser.getSellerProfile();
    }

    public Optional<Seller> getSellerByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getSellerProfile);
    }

    public Optional<Seller> getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId);
    }
}
