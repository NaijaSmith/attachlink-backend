package com.attachlink.controller;

import com.attachlink.dto.LogEntryRequest;
import com.attachlink.entity.User;
import com.attachlink.service.LogEntryService;
import com.attachlink.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final UserRepository userRepository;

    public LogEntryController(LogEntryService logEntryService,
                              UserRepository userRepository) {
        this.logEntryService = logEntryService;
        this.userRepository = userRepository;
    }

    // Submit log
    @PostMapping
    public ResponseEntity<?> submitLog(
            @RequestBody LogEntryRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow();

        return ResponseEntity.ok(
                logEntryService.createLog(request, student)
        );
    }

    // View my logs
    @GetMapping
    public ResponseEntity<List<?>> getMyLogs(Authentication authentication) {

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow();

        return ResponseEntity.ok(
                logEntryService.getStudentLogs(student)
        );
    }
}
