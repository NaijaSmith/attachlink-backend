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
package com.attachlink.service;

import com.attachlink.dto.RegisterRequest;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service handling user authentication and registration logic.
 * Optimized for a single User entity architecture to prevent complex OneToOne joins.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_ROLES = Set.of("STUDENT", "SUPERVISOR", "EMPLOYER", "ADMIN");

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves the currently logged-in user from the Security Context.
     */
    public User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user session not found."));
    }

    /**
     * Registers a new user. 
     * Handles specific field mapping for Students vs. Supervisors/Employers.
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 1. Normalize and Validate Role
        String role = (request.getRole() == null) 
                ? null 
                : request.getRole().trim().toUpperCase();

        if (role == null || !ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role. Must be one of: " + ALLOWED_ROLES);
        }

        // 2. Email Uniqueness Check
        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Email address is required.");
        }
        
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        // 3. Structural Validation (Field requirements per role)
        validateRegistrationData(role, request);

        // 4. Build Base User Entity
        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .fullName(request.getFullName().trim())
                .institutionName(request.getInstitutionName().trim())
                .active(true)
                .build();

        // 5. Apply Role-Specific Attributes
        if ("STUDENT".equals(role)) {
            user.setRegistrationNumber(request.getRegistrationNumber().trim());
            user.setCourse(request.getCourse().trim());
            
            // Link Supervisor and Employer for Students
            handleAssignments(user, request);
        }

        // Single save operation - no profile entity overhead
        return userRepository.save(user);
    }

    /**
     * Validates that a student's chosen supervisor/employer are valid and have the correct roles.
     */
    private void handleAssignments(User user, RegisterRequest request) {
        // Assign Supervisor (Academic)
        if (request.getSupervisorId() != null && request.getSupervisorId() > 0) {
            userRepository.findByIdAndRole(request.getSupervisorId(), "SUPERVISOR")
                .ifPresentOrElse(
                    user::setSupervisor,
                    () -> { throw new IllegalArgumentException("Selected Supervisor ID is invalid or not a supervisor."); }
                );
        }

        // Assign Employer (Industry)
        if (request.getEmployerId() != null && request.getEmployerId() > 0) {
            userRepository.findByIdAndRole(request.getEmployerId(), "EMPLOYER")
                .ifPresentOrElse(
                    user::setEmployer,
                    () -> { throw new IllegalArgumentException("Selected Employer ID is invalid or not an employer."); }
                );
        }
    }

    /**
     * Ensures mandatory fields are present based on the selected user role.
     */
    private void validateRegistrationData(String role, RegisterRequest request) {
        if (isBlank(request.getFullName())) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (isBlank(request.getInstitutionName())) {
            throw new IllegalArgumentException("Institution/Company name is required.");
        }
        if (isBlank(request.getPassword()) || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if ("STUDENT".equals(role)) {
            if (isBlank(request.getRegistrationNumber())) {
                throw new IllegalArgumentException("Registration Number is required for students.");
            }
            if (isBlank(request.getCourse())) {
                throw new IllegalArgumentException("Course name is required for students.");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}