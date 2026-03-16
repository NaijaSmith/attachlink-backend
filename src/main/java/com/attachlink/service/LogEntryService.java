/*Copyright 2026 Nicholas Kariuki Wambui

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.attachlink.service;

import com.attachlink.dto.LogEntryRequest;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

/**
 * Service handling the lifecycle and business logic of Log Entries.
 */
@Service
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;
    private final NotificationService notificationService;
    private final StorageService storageService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "docx", "doc");

    public LogEntryService(LogEntryRepository logEntryRepository, 
                           NotificationService notificationService,
                           StorageService storageService) {
        this.logEntryRepository = logEntryRepository;
        this.notificationService = notificationService;
        this.storageService = storageService;
    }

    /**
     * Creates a new log entry. 
     * FIXED: Parameter order matched to Controller: (request, student, attachment)
     */
    @Transactional
    public LogEntry createLog(LogEntryRequest request, User student, MultipartFile attachment) {
        if (logEntryRepository.existsByStudentAndLogDate(student, request.getLogDate())) {
            throw new RuntimeException("A log entry already exists for " + request.getLogDate());
        }

        LogEntry log = new LogEntry();
        log.setStudent(student);
        log.setLogDate(request.getLogDate());
        log.setActivities(request.getActivities());
        log.setChallenges(request.getChallenges());
        log.setLearningOutcomes(request.getLearningOutcomes());
        
        if (attachment != null && !attachment.isEmpty()) {
            validateFile(attachment);
            String filePath = storageService.store(attachment, "logs/" + student.getId());
            log.setAttachmentPath(filePath);
        }

        log.setStatus(LogStatus.SUBMITTED);
        log.setCreatedAt(LocalDateTime.now());

        LogEntry savedLog = logEntryRepository.save(log);

        if (student.getSupervisor() != null) {
            notificationService.notify(
                student.getSupervisor(),
                "New Attachment/Log: " + student.getFullName() + " submitted a log for " + log.getLogDate()
            );
        }

        return savedLog;
    }

    /**
     * FIXED: Added missing updateLogStatus method required by LogEntryController
     */
    @Transactional
    public void updateLogStatus(Long logId, LogStatus status) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log entry not found"));
        
        log.setStatus(status);
        if (status == LogStatus.APPROVED || status == LogStatus.REJECTED) {
            log.setReviewedAt(LocalDateTime.now());
        }
        logEntryRepository.save(log);
    }

    private void validateFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new RuntimeException("Invalid file name.");
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("Unsupported file type. Please upload PDF or DOCX.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB.");
        }
    }

    public List<LogEntry> getStudentLogs(User student) {
        return logEntryRepository.findByStudentOrderByLogDateDesc(student);
    }
}