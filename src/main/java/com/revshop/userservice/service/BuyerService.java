package com.revshop.userservice.service;

import com.revshop.userservice.model.Buyer;
import com.revshop.userservice.model.User;
import com.revshop.userservice.model.Role;
import com.revshop.userservice.repository.BuyerRepository;
import com.revshop.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class BuyerService {
    private static final Logger log = LoggerFactory.getLogger(BuyerService.class);
    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public BuyerService(BuyerRepository buyerRepository,
                        UserRepository userRepository,
                        EmailService emailService,
                        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.buyerRepository = buyerRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Buyer registerBuyer(User user) {
        log.info("Registering buyer email={}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        user.setRole(Role.BUYER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Buyer buyerProfile = new Buyer();
        buyerProfile.setUser(user);
        user.setBuyerProfile(buyerProfile);

        if (user.getAddresses() != null) {
            user.getAddresses().forEach(address -> address.setUser(user));
        }

        User savedUser = userRepository.save(user);
        emailService.sendUserRegistrationEmail(savedUser.getEmail(), savedUser.getName());
        return savedUser.getBuyerProfile();
    }

    public Optional<Buyer> getBuyerByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getBuyerProfile);
    }

    public Optional<Buyer> getBuyerById(Long buyerId) {
        return buyerRepository.findById(buyerId);
    }

    public Buyer updateBuyerProfile(Long buyerId, User updatedUserData) {
        log.info("Updating buyer profile id={}", buyerId);
        User existingUser = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(updatedUserData.getName());
        existingUser.setPhone(updatedUserData.getPhone());

        if (updatedUserData.getAddresses() != null) {
            existingUser.getAddresses().clear();
            updatedUserData.getAddresses().forEach(address -> {
                address.setUser(existingUser);
                existingUser.getAddresses().add(address);
            });
        }

        User savedUser = userRepository.save(existingUser);
        return savedUser.getBuyerProfile();
    }
}
