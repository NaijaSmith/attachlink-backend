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

import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import com.attachlink.service.ReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

/**
 * Controller for generating and downloading attachment progress reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves summary data for the authenticated student's report.
     */
    @GetMapping("/student")
    public ResponseEntity<?> getStudentReport(Authentication authentication) {
        User student = getCurrentUser(authentication);
        return ResponseEntity.ok(reportService.generateStudentReport(student));
    }

    /**
     * Allows supervisors or admins to view reports for a specific student.
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> getReportByStudentId(@PathVariable Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return ResponseEntity.ok(reportService.generateStudentReport(student));
    }

    /**
     * Exports the attachment logbook report as a PDF.
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportReportToPdf(Authentication authentication) {
        User student = getCurrentUser(authentication);
        var studentReport = reportService.generateStudentReport(student);
        byte[] pdfBytes = reportService.exportReportAsPdf(studentReport);
        Resource file = new ByteArrayResource(pdfBytes);

        String fileName = "Attachment_Report_" + student.getFirstName() + "_" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User record not found"));
    }
}