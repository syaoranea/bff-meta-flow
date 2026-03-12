package com.metasflow.bff.domain.user;

import com.metasflow.bff.config.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: User already exists with email {}", request.getEmail());
            throw new RuntimeException("Usuário já cadastrado com este e-mail");
        }

        String encodedPassword = (request.getPassword() != null && !request.getPassword().isEmpty())
                ? passwordEncoder.encode(request.getPassword())
                : passwordEncoder.encode("default_password");

        var user = User.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .goal(request.getGoal())
                .password(encodedPassword)
                .role(Role.USER)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching profile for user: {}", email);
        return repository.findByEmail(email)
                .map(user -> {
                    log.info("User found in database: {}", user.getEmail());
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("User not found in database for email: {}", email);
                    return new RuntimeException("Usuário não encontrado");
                });
    }

    public List<User> debugGetAllUsers() {
        return repository.findAll();
    }
}
