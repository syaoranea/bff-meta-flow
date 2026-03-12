package com.metasflow.bff.controller;

import com.metasflow.bff.domain.user.AuthResponse;
import com.metasflow.bff.domain.user.AuthService;
import com.metasflow.bff.domain.user.LoginRequest;
import com.metasflow.bff.domain.user.RegisterRequest;
import com.metasflow.bff.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(service.getCurrentUser());
    }
}
