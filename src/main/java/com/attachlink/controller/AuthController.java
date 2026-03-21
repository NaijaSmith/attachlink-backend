/*
 * Copyright 2026 Nicholas Kariuki Wambui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.attachlink.controller;

import com.attachlink.dto.RegisterRequest;
import com.attachlink.dto.UserMeResponse;
import com.attachlink.dto.UserSummaryDTO;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import com.attachlink.security.JwtUtil;
import com.attachlink.service.AuthService;
import com.attachlink.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for authentication-related endpoints.
 * Refined to support @Valid DTO constraints and snake_case mapping for dropdowns.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          AuthService authService,
                          UserRepository userRepository,
                          PasswordResetService passwordResetService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
    }

    /**
     * LOGIN
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest loginData) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginData.getEmail(), 
                            loginData.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(loginData.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer"
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is disabled"));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is locked"));
        }
    }

    /**
     * REGISTER
     * Refined: Added @Valid to trigger automatic DTO validation.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred during registration"));
        }
    }

    /**
     * Fetch available Academic Supervisors.
     */
    @GetMapping("/list-supervisors")
    public ResponseEntity<List<UserSummaryDTO>> getSupervisors() {
        List<UserSummaryDTO> supervisors = userRepository.findAll().stream()
                .filter(u -> "SUPERVISOR".equalsIgnoreCase(u.getRole()))
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(supervisors);
    }

    /**
     * Fetch available Industry Employers.
     */
    @GetMapping("/list-employers")
    public ResponseEntity<List<UserSummaryDTO>> getEmployers() {
        List<UserSummaryDTO> employers = userRepository.findAll().stream()
                .filter(u -> "EMPLOYER".equalsIgnoreCase(u.getRole()))
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employers);
    }

    /**
     * Helper to map User Entity to UserSummaryDTO.
     * Uses the refined DTO which handles snake_case for the Android client.
     */
    private UserSummaryDTO convertToSummaryDTO(User user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .institutionName(user.getInstitutionName())
                .registrationNumber(user.getRegistrationNumber())
                .build();
    }

    /**
     * FORGOT PASSWORD
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.processForgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not send OTP."));
        }
    }

    /**
     * RESET PASSWORD
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, 
                                          @RequestParam String otp, 
                                          @RequestParam String newPassword) {
        boolean success = passwordResetService.verifyAndResetPassword(email, otp, newPassword);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password reset successful!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired OTP."));
        }
    }

    /**
     * GET CURRENT USER INFO
     */
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User profile not found"));

        UserMeResponse res = new UserMeResponse(
                user.getEmail(),
                user.getRole(),
                user.getFullName(),
                user.getRegistrationNumber(),
                user.getInstitutionName(),
                user.getCourse()
        );

        return ResponseEntity.ok(res);
    }
}