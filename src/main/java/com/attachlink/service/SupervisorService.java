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
package com.attachlink.service;

import com.attachlink.dto.LogReviewRequest;
import com.attachlink.entity.Evaluation;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.EvaluationRepository;
import com.attachlink.repository.LogEntryRepository;
import com.attachlink.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service handling the business logic for Supervisors.
 * Manages dashboard statistics, student assignments, and log reviews.
 */
@Service
public class SupervisorService {

    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final NotificationService notificationService;

    public SupervisorService(LogEntryRepository logEntryRepository,
                             UserRepository userRepository,
                             EvaluationRepository evaluationRepository,
                             NotificationService notificationService) {
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
        this.evaluationRepository = evaluationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves dashboard statistics for a supervisor including student count and pending tasks.
     */
    public Map<String, Object> getDashboardStats(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor not found with email: " + email));

        long totalStudents = userRepository.countBySupervisorAndActiveTrue(supervisor);
        long pendingLogs = logEntryRepository.countByStudent_SupervisorAndStatus(supervisor, LogStatus.SUBMITTED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("pendingLogsCount", pendingLogs);
        stats.put("timestamp", LocalDateTime.now());
        
        return stats;
    }

    /**
     * Retrieves a list of students specifically assigned to this supervisor.
     */
    public List<User> getAssignedStudents(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));
        return userRepository.findAllBySupervisor(supervisor);
    }

    /**
     * Retrieves all logs awaiting review for students assigned to this supervisor.
     */
    public List<LogEntry> getPendingLogsForSupervisor(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor account not found"));

        return logEntryRepository.findAllByStudent_SupervisorAndStatus(supervisor, LogStatus.SUBMITTED);
    }

    /**
     * Review a specific log entry, updates status, and creates an Evaluation record.
     * Uses @Transactional to ensure both LogEntry update and Evaluation creation succeed together.
     */
    @Transactional
    public LogEntry reviewLog(Long logId, LogReviewRequest request, String supervisorEmail) {
        User supervisor = userRepository.findByEmail(supervisorEmail)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log entry not found with ID: " + logId));

        // SECURITY CHECK: Ensure this supervisor is actually assigned to the student
        User student = log.getStudent();
        if (student.getSupervisor() == null || !student.getSupervisor().getId().equals(supervisor.getId())) {
            throw new SecurityException("Unauthorized: You are not assigned to supervise this student.");
        }

        // VALIDATION: Ensure the log hasn't been processed already
        if (!LogStatus.SUBMITTED.equals(log.getStatus())) {
            throw new IllegalStateException("Log entry is currently in " + log.getStatus() + " status and cannot be reviewed.");
        }

        // 1. Update Log Status and Metadata
        log.setStatus(request.getStatus()); // Expecting APPROVED or REJECTED
        log.setReviewedAt(LocalDateTime.now());
        LogEntry savedLog = logEntryRepository.save(log);

        // 2. Create and Persist Evaluation/Feedback
        Evaluation evaluation = Evaluation.builder()
                .logEntry(savedLog)
                .supervisor(supervisor)
                .remarks(request.getSupervisorComment())
                .score(request.getScore())
                .submittedAt(LocalDateTime.now())
                .build();
        
        evaluationRepository.save(evaluation);

        // 3. Trigger Notification to Student
        String message = String.format("Log Update: Your entry for %s has been %s by %s.", 
                log.getLogDate(), 
                request.getStatus().toString().toLowerCase(),
                supervisor.getFullName());
                
        notificationService.notify(student, message);

        return savedLog;
    }
}