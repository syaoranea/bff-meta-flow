package com.metasflow.bff.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/register")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/login")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/health")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/debug/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/me")).authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/health")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/suggestions"), AntPathRequestMatcher.antMatcher("/api/suggestions/**")).authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/goals"), AntPathRequestMatcher.antMatcher("/api/goals/**")).authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
