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

package com.attachlink.service;

import com.attachlink.dto.LogEntryRequest;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

/**
 * Service handling the lifecycle and business logic of Log Entries.
 * Supports initial submission, supervisor review, and student resubmission of rejected logs.
 */
@Service
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;
    private final NotificationService notificationService;
    private final StorageService storageService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "docx", "jpg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public LogEntryService(LogEntryRepository logEntryRepository, 
                           NotificationService notificationService,
                           StorageService storageService) {
        this.logEntryRepository = logEntryRepository;
        this.notificationService = notificationService;
        this.storageService = storageService;
    }

    /**
     * Creates a new log entry.
     */
    @Transactional
    public LogEntry createLog(LogEntryRequest request, User student, MultipartFile attachment) {
        if (logEntryRepository.existsByStudentAndLogDate(student, request.getLogDate())) {
            throw new IllegalArgumentException("A log entry already exists for " + request.getLogDate());
        }

        LogEntry log = new LogEntry();
        log.setStudent(student);
        log.setLogDate(request.getLogDate());
        log.setActivities(request.getActivities() != null ? request.getActivities() : "No activities listed");
        log.setChallenges(request.getChallenges());
        log.setLearningOutcomes(request.getLearningOutcomes());
        
        handleFileAttachment(log, student, attachment);

        log.setStatus(LogStatus.SUBMITTED);
        log.setSubmittedAt(LocalDateTime.now());

        LogEntry savedLog = logEntryRepository.save(log);

        if (student.getSupervisor() != null) {
            notificationService.notify(
                student.getSupervisor(),
                String.format("New Log: %s submitted a log for %s", 
                    student.getFullName(), log.getLogDate())
            );
        }

        return savedLog;
    }

    /**
     * Handles student resubmission of a REJECTED log.
     * Updates text fields, optionally replaces the file, and resets status to SUBMITTED.
     */
    @Transactional
    public LogEntry resubmitLog(Long logId, LogEntryRequest request, User student, MultipartFile attachment) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with ID: " + logId));

        // 1. Security Check: Ownership
        if (!log.getStudent().getId().equals(student.getId())) {
            throw new SecurityException("Unauthorized: You can only edit your own logs.");
        }

        // 2. State Check: Must be REJECTED
        if (!LogStatus.REJECTED.equals(log.getStatus())) {
            throw new IllegalStateException("Only rejected logs can be edited and resubmitted.");
        }

        // 3. Update Text Content
        log.setActivities(request.getActivities());
        log.setChallenges(request.getChallenges());
        log.setLearningOutcomes(request.getLearningOutcomes());
        
        // 4. Update File (If a new one is provided)
        if (attachment != null && !attachment.isEmpty()) {
            handleFileAttachment(log, student, attachment);
        }

        // 5. Reset Status & Track Update Time
        log.setStatus(LogStatus.SUBMITTED);
        log.setUpdatedAt(LocalDateTime.now());

        LogEntry updatedLog = logEntryRepository.save(log);

        // 6. Notify Supervisor of the Correction
        if (student.getSupervisor() != null) {
            notificationService.notify(
                student.getSupervisor(),
                String.format("Log Updated: %s resubmitted the log for %s", 
                    student.getFullName(), log.getLogDate())
            );
        }

        return updatedLog;
    }

    /**
     * Internal helper to process and store file attachments.
     */
    private void handleFileAttachment(LogEntry log, User student, MultipartFile attachment) {
        if (attachment != null && !attachment.isEmpty()) {
            validateFile(attachment);
            String subDirectory = "logs/" + student.getId();
            String filePath = storageService.store(attachment, subDirectory);
            log.setAttachmentPath(filePath);
            log.setAttachmentOriginalName(attachment.getOriginalFilename());
        }
    }

    @Transactional
    public LogEntry reviewLog(Long logId, LogStatus status, String supervisorComment) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with ID: " + logId));
        
        log.setStatus(status);
        log.setSupervisorComment(supervisorComment);
        log.setReviewedAt(LocalDateTime.now());
        
        LogEntry updatedLog = logEntryRepository.save(log);

        notificationService.notify(
            log.getStudent(),
            String.format("Log Review: Your log for %s has been %s", 
                log.getLogDate(), status.name().toLowerCase())
        );

        return updatedLog;
    }

    @Transactional(readOnly = true)
    public List<LogEntry> getStudentLogs(User student) {
        return logEntryRepository.findByStudentOrderByLogDateDesc(student);
    }

    private void validateFile(MultipartFile file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (filename.contains("..")) {
            throw new IllegalArgumentException("Invalid file path sequence in " + filename);
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file type. Allowed: " + ALLOWED_EXTENSIONS);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the 5MB limit.");
        }
    }

    @Transactional
    public void updateLogStatus(Long logId, LogStatus status) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with ID: " + logId));
        log.setStatus(status);
        log.setReviewedAt(LocalDateTime.now());
        logEntryRepository.save(log);
    }
}
