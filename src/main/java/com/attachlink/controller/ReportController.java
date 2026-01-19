package com.attachlink.controller;

import com.attachlink.repository.UserRepository;
import com.attachlink.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportController(
            ReportService reportService,
            UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentReport(
            Authentication authentication) {

        var student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                reportService.generateStudentReport(student)
        );
    }
}
