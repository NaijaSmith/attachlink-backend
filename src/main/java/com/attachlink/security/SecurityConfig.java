/*
 * Copyright 2026 Nicholas Kariuki Wambui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.attachlink.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Centralized Security Configuration for AttachLink.
 * Configured for Stateless JWT Auth with explicit Role-Based Access Control.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Allows using @PreAuthorize on Service/Controller methods
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // 1. Public Endpoints (No Token Required)
                .requestMatchers(
                    "/api/auth/login", 
                    "/api/auth/register", 
                    "/api/auth/forgot-password", 
                    "/api/auth/reset-password",
                    "/api/auth/list-supervisors",
                    "/api/auth/list-employers"
                ).permitAll()

                // 2. Supervisor-Specific Endpoints
                .requestMatchers("/api/supervisor/**").hasAnyAuthority("ROLE_SUPERVISOR", "SUPERVISOR")

                // 3. Employer-Specific Endpoints
                .requestMatchers("/api/employer/**").hasAnyAuthority("ROLE_EMPLOYER", "EMPLOYER")

                // 4. Student-Specific Endpoints (Including Log Submissions/Resubmissions)
                .requestMatchers("/api/logs/**").hasAnyAuthority("ROLE_STUDENT", "STUDENT")

                // 5. Shared Authenticated Resources
                .requestMatchers(
                    "/api/auth/me",
                    "/api/notifications/**",
                    "/api/analytics/**",
                    "/api/reports/**",
                    "/api/fcm/**",
                    "/api/email/**"
                ).authenticated()

                // 6. Final Catch-all
                .anyRequest().authenticated()
            )
            // Inject our JWT Filter before the standard UsernamePassword filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for Mobile and Web clients.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // For production 2026, replace "*" with your specific domain if applicable
        config.setAllowedOrigins(List.of("*")); 
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
