package com.revshop.userservice.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final RestTemplate restTemplate;
    private final String NOTIFICATION_SERVICE_URL = "http://notification-service/api/notifications";

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendUserRegistrationEmail(String email, String name) {
        log.info("Sending registration email request to Notification Service for {} ({})", name, email);
        try {
            Map<String, String> request = Map.of("toEmail", email, "name", name);
            restTemplate.postForEntity(NOTIFICATION_SERVICE_URL + "/email/registration", request, Void.class);
        } catch (Exception e) {
            log.error("Failed to call Notification Service for registration email: {}", e.getMessage());
        }
    }

    public void sendOtpEmail(String email, String otp) {
        log.info("Sending OTP email request to Notification Service for {} with OTP: {}", email, otp);
        try {
            Map<String, String> request = Map.of("to", email, "otp", otp);
            restTemplate.postForEntity(NOTIFICATION_SERVICE_URL + "/otp/email", request, Void.class);
        } catch (Exception e) {
            log.error("Failed to call Notification Service for OTP email: {}", e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String email, String token) {
        log.info("Sending password reset email request to Notification Service for {} with token: {}", email, token);
        try {
            String resetLink = "http://localhost:4200/reset-password?token=" + token;
            Map<String, String> request = Map.of("to", email, "resetLink", resetLink);
            restTemplate.postForEntity(NOTIFICATION_SERVICE_URL + "/email/password-reset", request, Void.class);
        } catch (Exception e) {
            log.error("Failed to call Notification Service for password reset email: {}", e.getMessage());
        }
    }
}
