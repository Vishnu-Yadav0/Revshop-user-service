package com.revshop.userservice.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendUserRegistrationEmail(String email, String name) {
        log.info("Sending registration email to {} ({})", name, email);
    }

    public void sendOtpEmail(String email, String otp) {
        log.info("Sending OTP email to {} with OTP: {}", email, otp);
    }

    public void sendPasswordResetEmail(String email, String token) {
        log.info("Sending password reset email to {} with token: {}", email, token);
    }
}
