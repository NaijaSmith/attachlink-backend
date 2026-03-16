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

import com.attachlink.dto.LogEntryRequest;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.User;
import com.attachlink.entity.LogStatus;
import com.attachlink.service.LogEntryService;
import com.attachlink.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing student daily log entries and file uploads.
 */
@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final UserRepository userRepository;

    public LogEntryController(LogEntryService logEntryService, UserRepository userRepository) {
        this.logEntryService = logEntryService;
        this.userRepository = userRepository;
    }

    /**
     * Submit a daily log entry with an optional file attachment.
     * Uses MULTIPART_FORM_DATA to allow JSON data and file binary to be sent together.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogEntry> submitLog(
            @RequestPart("data") LogEntryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User student = getCurrentUser(authentication);
        
        // Refined to match Service signature: createLog(request, attachment, student)
        // Note: Swapped 'student' and 'file' based on the error shown in your screenshot
        LogEntry savedLog = logEntryService.createLog(request, student, file);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    /**
     * Retrieves all log entries belonging to the currently authenticated student.
     */
    @GetMapping
    public ResponseEntity<List<LogEntry>> getMyLogs(Authentication authentication) {
        User student = getCurrentUser(authentication);
        return ResponseEntity.ok(logEntryService.getStudentLogs(student));
    }

    /**
     * Allows updating the status of a specific log (e.g., from SUBMITTED to APPROVED).
     */
    @PatchMapping("/{logId}/status")
    public ResponseEntity<Void> updateLogStatus(
            @PathVariable Long logId,
            @RequestParam String status) {
        
        try {
            LogStatus logStatus = LogStatus.valueOf(status.toUpperCase());
            logEntryService.updateLogStatus(logId, logStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + status);
        }
    }

    /**
     * Helper method to extract the User entity from the security context.
     */
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}