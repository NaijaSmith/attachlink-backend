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
 * Controller for managing student daily log entries and resubmissions.
 * Supports multipart uploads for initial submission and rejected log correction.
 */
@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final UserRepository userRepository;
    
    // Base directory for uploads
    private final Path rootLocation = Paths.get("upload-dir");

    public LogEntryController(LogEntryService logEntryService, UserRepository userRepository) {
        this.logEntryService = logEntryService;
        this.userRepository = userRepository;
    }

    /**
     * Submit a brand new daily log entry.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogEntry> submitLog(
            @RequestPart("data") LogEntryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User student = getCurrentUser(authentication);
        LogEntry savedLog = logEntryService.createLog(request, student, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    /**
     * RESUBMIT A REJECTED LOG.
     * Allows students to edit content and set status back to SUBMITTED after a rejection.
     */
    @PutMapping(value = "/{logId}/resubmit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogEntry> resubmitLog(
            @PathVariable Long logId,
            @RequestPart("data") LogEntryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User student = getCurrentUser(authentication);
        
        // Service logic ensures log belongs to student and is currently REJECTED
        LogEntry updatedLog = logEntryService.resubmitLog(logId, request, student, file);
        
        return ResponseEntity.ok(updatedLog);
    }

    /**
     * Retrieves all log entries for the authenticated student.
     */
    @GetMapping
    public ResponseEntity<List<LogEntry>> getMyLogs(Authentication authentication) {
        User student = getCurrentUser(authentication);
        return ResponseEntity.ok(logEntryService.getStudentLogs(student));
    }

    /**
     * Update log status (Internal use/Supervisor manual triggers).
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
     * Download log attachment with path traversal protection.
     */
   @GetMapping("/download/{*path}")
public ResponseEntity<Resource> downloadAttachment(@PathVariable String path) {
    try {
        // 1. Clean the incoming path from the URL
        // Example: "/logs/4/file.pdf" -> "logs/4/file.pdf"
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        
        // 2. Use Absolute Paths for the security check. 
        // This is crucial for Railway/Cloud environments where relative paths 
        // can resolve to unexpected root locations.
        Path rootAbs = rootLocation.toAbsolutePath().normalize();
        Path fileAbs = rootAbs.resolve(cleanPath).normalize();
        
        // 3. Path Traversal Protection: 
        // Ensure the requested file is physically located inside the upload directory.
        if (!fileAbs.startsWith(rootAbs)) {
            // Log this for debugging purposes during your presentation prep
            System.err.println("Security Block: Attempted access outside root: " + fileAbs);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Resource resource = new UrlResource(fileAbs.toUri());

        // 4. File existence and readability check
        if (resource.exists() && resource.isReadable()) {
            String filename = fileAbs.getFileName().toString();
            
            // Set content type to OCTET_STREAM to force a download in the browser/app
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
    } catch (MalformedURLException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (Exception e) {
        // Catch-all for IO errors or permission issues on the server disk
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    /**
     * Extracts user from session.
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
