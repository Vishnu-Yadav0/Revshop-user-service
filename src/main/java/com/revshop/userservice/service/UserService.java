package com.revshop.userservice.service;

import com.revshop.userservice.dto.*;
import com.revshop.userservice.model.User;
import com.revshop.userservice.repository.UserRepository;
import com.revshop.userservice.exception.*;
import com.revshop.userservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.revshop.userservice.model.PasswordResetToken;
import com.revshop.userservice.repository.PasswordResetTokenRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       PasswordResetTokenRepository tokenRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidInputException("Email already registered: " + request.getEmail());
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setAge(request.getAge());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole().name(), saved.getUserId());
        return new AuthResponse(token, saved.getUserId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.isActive()) {
            throw new InvalidInputException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidInputException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());
        return new AuthResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse loginWithReactivation(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidInputException("Invalid credentials");
        }
        
        // Reactivate if inactive
        if (!user.isActive()) {
            user.setActive(true);
            user = userRepository.save(user);
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());
        return new AuthResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, User updatedUserData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setName(updatedUserData.getName());
        user.setPhone(updatedUserData.getPhone());
        user.setAge(updatedUserData.getAge());
        return toResponse(userRepository.save(user));
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String getSecurityQuestionByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getSecurityQuestion();
    }

    public void resetPasswordBySecurity(String email, String answer, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (user.getSecurityAnswer() != null && user.getSecurityAnswer().equalsIgnoreCase(answer)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid security answer");
        }
    }

    public User reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setActive(true);
        return userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setActive(false);
        user.setName("Deleted User");
        user.setEmail("deleted_" + java.util.UUID.randomUUID().toString() + "@revshop.com");
        user.setPhone(null);
        userRepository.save(user);
    }

    @Transactional
    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Delete any existing tokens for this user to avoid duplicate keys
        tokenRepository.deleteByUser(user);
        
        String token = java.util.UUID.randomUUID().toString();
        PasswordResetToken resetToken = 
            new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPasswordWithToken(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setUserId(user.getUserId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole());
        r.setPhone(user.getPhone());
        r.setAge(user.getAge());
        r.setActive(user.isActive());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }
}
