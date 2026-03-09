/*
 * Copyright 2026 Nicholas Kariuki Wambui
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * * http://www.apache.org/licenses/LICENSE-2.0
 * * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.attachlink.controller;

import com.attachlink.dto.LogEntryRequest;
import com.attachlink.entity.User;
import com.attachlink.entity.LogStatus;
import com.attachlink.service.LogEntryService;
import com.attachlink.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Submit a daily log entry.
     * Refined to handle user context directly from Security Authentication.
     */
    @PostMapping
    public ResponseEntity<?> submitLog(
            @RequestBody LogEntryRequest request,
            Authentication authentication) {

        User student = getCurrentUser(authentication);

        // createLog will now trigger the FCM notification logic 
        // to alert supervisors of new submissions.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logEntryService.createLog(request, student));
    }

    /**
     * Retrieve logs for the authenticated student.
     */
    @GetMapping
    public ResponseEntity<List<?>> getMyLogs(Authentication authentication) {
        User student = getCurrentUser(authentication);
        return ResponseEntity.ok(logEntryService.getStudentLogs(student));
    }

    /**
     * Supervisor endpoint to approve or flag a log.
     * This will trigger the FCM Push Notification to the student.
     */
    @PatchMapping("/{logId}/status")
    public ResponseEntity<?> updateLogStatus(
            @PathVariable Long logId,
            @RequestParam String status,
            @RequestParam(required = false) String comment,
            Authentication authentication) {
        
        // Ensure the person making the change has the right authority
        // Logic for checking SUPERVISOR role can be added here or via @PreAuthorize
        logEntryService.updateLogStatus(logId, LogStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}