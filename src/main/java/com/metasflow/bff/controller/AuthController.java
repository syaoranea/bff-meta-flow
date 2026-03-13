package com.metasflow.bff.controller;

import com.metasflow.bff.domain.user.AuthResponse;
import com.metasflow.bff.domain.user.AuthService;
import com.metasflow.bff.domain.user.LoginRequest;
import com.metasflow.bff.domain.user.RegisterRequest;
import com.metasflow.bff.domain.user.UpdateProfileRequest;
import com.metasflow.bff.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService service;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("version", "1.0.0");
        status.put("message", "BFF Meta Flow is running");
        return ResponseEntity.ok(status);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/debug/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.debugGetAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(service.getCurrentUser());
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateMe(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(service.updateProfile(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}
