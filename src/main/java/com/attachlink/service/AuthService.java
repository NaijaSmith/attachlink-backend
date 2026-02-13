/*Copyright 2026 Nicholas Kariuki Wambui

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.attachlink.service;

import com.attachlink.dto.RegisterRequest;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {

        // Normalize role
        String role = request.getRole() == null
                ? null
                : request.getRole().trim().toUpperCase();

        // 1️⃣ Validate role
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role provided");
        }

        // 2️⃣ Enforce STUDENT-only fields
        if ("STUDENT".equals(role)) {
            if (isBlank(request.getFullName())
                    || isBlank(request.getRegistrationNumber())) {

                throw new IllegalArgumentException(
                        "Students must provide full name and registration number"
                );
            }
        }

        // 3️⃣ Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 4️⃣ Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // Only students get these fields populated
        if ("STUDENT".equals(role)) {
            user.setFullName(request.getFullName());
            user.setRegistrationNumber(request.getRegistrationNumber());
        }

        userRepository.save(user);
    }

    // ===== Helpers =====

    private boolean isValidRole(String role) {
        return "STUDENT".equals(role)
            || "SUPERVISOR".equals(role)
            || "EMPLOYER".equals(role)
            || "ADMIN".equals(role);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
