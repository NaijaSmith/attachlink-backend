package com.attachlink.controller;

import com.attachlink.repository.UserRepository;
import com.attachlink.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(
            AnalyticsService analyticsService,
            UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentAnalytics(
            Authentication authentication) {

        var student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                analyticsService.getStudentAnalytics(student)
        );
    }
}
