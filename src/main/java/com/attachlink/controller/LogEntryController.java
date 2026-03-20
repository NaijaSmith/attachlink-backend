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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller for managing student daily log entries and file uploads.
 * Refined to fix syntax errors and improve file download handling.
 */
@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final UserRepository userRepository;
    // Base directory for uploads, should match your StorageService configuration
    private final Path rootLocation = Paths.get("upload-dir");

    public LogEntryController(LogEntryService logEntryService, UserRepository userRepository) {
        this.logEntryService = logEntryService;
        this.userRepository = userRepository;
    }

    /**
     * Submit a daily log entry with an optional file attachment.
     * Fixed syntax: Removed improper escaping and corrected parameter grouping.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogEntry> submitLog(
            @RequestPart("data") LogEntryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User student = getCurrentUser(authentication);
        
        // Pass the request, the authenticated student, and the optional file to the service
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
     * Download attached file by relative path (e.g., logs/1/uuid_file.pdf).
     * Refined path resolution and error handling.
     */
    @GetMapping("/download/{*path}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String path) {
        try {
            // Remove leading slash if present to prevent absolute path traversal
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            Path filePath = rootLocation.resolve(cleanPath).normalize();
            
            // Security check: Ensure the resolved path is still within the rootLocation
            if (!filePath.startsWith(rootLocation.normalize())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String filename = filePath.getFileName().toString();
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to extract the User entity from the security context.
     */
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}