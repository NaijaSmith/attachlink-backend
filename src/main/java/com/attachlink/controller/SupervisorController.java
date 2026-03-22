/*
 * Copyright (c) 2026 Nicholas Kariuki. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Nicholas Kariuki ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into.
 *
 * Project: AttachLink
 * Author: Nicholas Kariuki
 */
package com.attachlink.controller;

import com.attachlink.dto.LogReviewRequest;
import com.attachlink.service.SupervisorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST Controller for Supervisor operations.
 * Optimized to handle Student metadata (Name/RegNo) and validated log reviews.
 */
@RestController
@RequestMapping("/api/supervisor")
@PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ROLE_SUPERVISOR')")
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    /**
     * Get a dashboard summary for the supervisor.
     */
    @GetMapping("/dashboard/summary")
    public ResponseEntity<?> getDashboardSummary(Principal principal) {
        return ResponseEntity.ok(
                supervisorService.getDashboardStats(principal.getName())
        );
    }

    /**
     * View all students currently assigned to this supervisor.
     */
    @GetMapping("/students")
    public ResponseEntity<?> getAssignedStudents(Principal principal) {
        return ResponseEntity.ok(
                supervisorService.getAssignedStudents(principal.getName())
        );
    }

    /**
     * View logs awaiting review.
     * Refined to include Student summary (Name and Reg Number) in the response list.
     */
    @GetMapping("/logs/pending")
    public ResponseEntity<?> viewSubmittedLogs(Principal principal) {
        return ResponseEntity.ok(
                supervisorService.getPendingLogsForSupervisor(principal.getName())
        );
    }

    /**
     * Review a specific log entry.
     * Added @Valid to ensure the supervisor_comment and score meet DTO constraints.
     */
    @PatchMapping("/logs/{logId}/review")
    public ResponseEntity<?> reviewLog(
            @PathVariable Long logId,
            @Valid @RequestBody LogReviewRequest request,
            Principal principal) {

        return ResponseEntity.ok(
                supervisorService.reviewLog(logId, request, principal.getName())
        );
    }
}