package com.revshop.userservice.controller;

import com.revshop.userservice.dto.ApiResponse;
import com.revshop.userservice.dto.PasswordUpdateRequest;
import com.revshop.userservice.dto.UserResponse;
import com.revshop.userservice.model.User;
import com.revshop.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("GET /api/users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>("All users fetched", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>("User fetched", user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{}", email);
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("User fetched", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(@PathVariable Long id, @RequestBody User updatedUser) {
        log.info("PUT /api/users/{}", id);
        UserResponse user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(new ApiResponse<>("User updated successfully", user));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateRequest request) {
        log.info("PUT /api/users/{}/password", id);
        userService.updatePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse<>("Password updated successfully", null));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateAccount(@PathVariable Long id) {
        log.info("PUT /api/users/{}/deactivate", id);
        userService.deactivateUser(id);
        return ResponseEntity.ok(new ApiResponse<>("Account deactivated", null));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<String>> reactivateAccount(@PathVariable Long id) {
        log.info("PUT /api/users/{}/reactivate", id);
        userService.reactivateUser(id);
        return ResponseEntity.ok(new ApiResponse<>("Account reactivated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("Account deleted", null));
    }

    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse<java.util.Map<String, Boolean>>> checkEmail(@RequestParam String email) {
        log.info("GET /api/users/check/email?email={}", email);
        return ResponseEntity.ok(new ApiResponse<>("Email check results", java.util.Map.of("exists", userService.emailExists(email))));
    }
}
