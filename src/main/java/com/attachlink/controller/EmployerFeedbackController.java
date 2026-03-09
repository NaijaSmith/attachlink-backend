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

import com.attachlink.dto.EmployerFeedbackRequest;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import com.attachlink.service.EmployerFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Controller for handling feedback submitted by employers regarding attachments/internships.
 */
@RestController
@RequestMapping("/api/employer")
public class EmployerFeedbackController {

    private final EmployerFeedbackService feedbackService;
    private final UserRepository userRepository;

    public EmployerFeedbackController(
            EmployerFeedbackService feedbackService,
            UserRepository userRepository) {
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
    }

    /**
     * Employer submits feedback for a specific attachment/student.
     * * @param request The feedback data including ratings and comments.
     * @param authentication The current authenticated employer.
     * @return Success message or error details.
     */
    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(
            @RequestBody EmployerFeedbackRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // Retrieve the employer entity
        User employer = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employer profile not found"));

        try {
            // Processing feedback submission via service
            Object result = feedbackService.submitFeedback(request, employer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors (e.g., student not found, already submitted)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Generic error handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred during submission"));
        }
    }
}