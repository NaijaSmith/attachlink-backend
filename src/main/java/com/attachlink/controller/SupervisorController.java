package com.attachlink.controller;

import com.attachlink.dto.LogReviewRequest;
import com.attachlink.service.SupervisorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor")
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    // View logs awaiting review
    @GetMapping("/logs")
    public ResponseEntity<?> viewSubmittedLogs() {
        return ResponseEntity.ok(
                supervisorService.getSubmittedLogs()
        );
    }

    // Review log
    @PostMapping("/logs/{logId}/review")
    public ResponseEntity<?> reviewLog(
            @PathVariable Long logId,
            @RequestBody LogReviewRequest request) {

        return ResponseEntity.ok(
                supervisorService.reviewLog(logId, request)
        );
    }
}
