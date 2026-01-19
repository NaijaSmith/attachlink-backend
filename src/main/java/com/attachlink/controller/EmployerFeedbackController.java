package com.attachlink.controller;

import com.attachlink.dto.EmployerFeedbackRequest;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import com.attachlink.service.EmployerFeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    // Employer submits feedback
    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(
            @RequestBody EmployerFeedbackRequest request,
            Authentication authentication) {

        User employer = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                feedbackService.submitFeedback(request, employer)
        );
    }
}
