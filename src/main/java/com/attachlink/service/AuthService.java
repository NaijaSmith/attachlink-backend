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
import com.attachlink.entity.Student;
import com.attachlink.entity.Supervisor;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service handling user authentication and registration logic.
 * Updated to support decoupled User, Student, and Supervisor entities.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_ROLES = Set.of("STUDENT", "SUPERVISOR", "EMPLOYER", "ADMIN");

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Helper method to retrieve the currently logged-in user from the Security Context.
     * This fixes the "undefined method" error in VerificationService.
     */
    public User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    /**
     * Registers a new user and creates their respective profile (Student/Supervisor).
     */
    @Transactional
    public User register(RegisterRequest request) {
        String role = (request.getRole() == null) 
                ? null 
                : request.getRole().trim().toUpperCase();

        if (role == null || !ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role. Allowed roles: " + ALLOWED_ROLES);
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Email is required.");
        }
        
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        validateRegistrationData(role, request);

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .fullName(request.getFullName().trim())
                .institutionName(request.getInstitutionName().trim())
                .active(true)
                .build();

        if ("STUDENT".equals(role)) {
            createStudentProfile(user, request);
        } else if ("SUPERVISOR".equals(role)) {
            createSupervisorProfile(user, request);
        }

        handleAssignments(user, request);

        return userRepository.save(user);
    }

    private void createStudentProfile(User user, RegisterRequest request) {
        Student student = new Student();
        student.setUser(user);
        student.setRegistrationNumber(request.getRegistrationNumber().trim());
        student.setCourse(request.getCourse().trim());
        user.setStudentProfile(student);
    }

    private void createSupervisorProfile(User user, RegisterRequest request) {
        Supervisor supervisor = new Supervisor();
        supervisor.setUser(user);
        user.setSupervisorProfile(supervisor);
    }

    private void handleAssignments(User user, RegisterRequest request) {
        if (request.getSupervisorId() != null) {
            userRepository.findById(request.getSupervisorId())
                .ifPresent(user::setAssignedSupervisor);
        }
        if (request.getEmployerId() != null) {
            userRepository.findById(request.getEmployerId())
                .ifPresent(user::setAssignedEmployer);
        }
    }

    private void validateRegistrationData(String role, RegisterRequest request) {
        if (isBlank(request.getFullName())) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (isBlank(request.getInstitutionName())) {
            throw new IllegalArgumentException("Institution name is required.");
        }
        if ("STUDENT".equals(role)) {
            if (isBlank(request.getRegistrationNumber()) || isBlank(request.getCourse())) {
                throw new IllegalArgumentException("Students must provide Registration Number and Course.");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}