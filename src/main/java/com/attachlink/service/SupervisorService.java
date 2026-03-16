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
     * Retrieves dashboard statistics for a supervisor.
     * Fixed: Matches call in SupervisorController.getDashboardStats()
     */
    public Map<String, Object> getDashboardStats(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        long totalStudents = userRepository.countBySupervisor(supervisor);
        long pendingLogs = logEntryRepository.countByStudent_SupervisorAndStatus(supervisor, LogStatus.SUBMITTED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("pendingLogsCount", pendingLogs);
        return stats;
    }

    /**
     * Retrieves a list of students assigned to this supervisor.
     * Fixed: Matches call in SupervisorController.getAssignedStudents()
     */
    public List<User> getAssignedStudents(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));
        return userRepository.findAllBySupervisor(supervisor);
    }

    /**
     * Retrieves all logs awaiting review for students assigned to this supervisor.
     * Fixed: Matches call in SupervisorController.viewSubmittedLogs()
     */
    public List<LogEntry> getPendingLogsForSupervisor(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor account not found"));

        return logEntryRepository.findAllByStudent_SupervisorAndStatus(supervisor, LogStatus.SUBMITTED);
    }

    /**
     * Review a specific log entry and generate an Evaluation record.
     */
    @Transactional
    public LogEntry reviewLog(Long logId, LogReviewRequest request, String supervisorEmail) {
        User supervisor = userRepository.findByEmail(supervisorEmail)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log entry not found"));

        // SECURITY CHECK: Verify supervisor authorization
        User student = log.getStudent();
        if (student.getSupervisor() == null || !student.getSupervisor().getId().equals(supervisor.getId())) {
            throw new RuntimeException("Security Violation: Unauthorized access to student data.");
        }

        // Business Rule: Only review logs in SUBMITTED state
        if (!LogStatus.SUBMITTED.equals(log.getStatus())) {
            throw new RuntimeException("Log entry is not in a pending state.");
        }

        // 1. Update Log Status
        log.setStatus(request.getStatus());
        log.setReviewedAt(LocalDateTime.now());
        LogEntry savedLog = logEntryRepository.save(log);

        // 2. Create Evaluation Record
        Evaluation evaluation = new Evaluation();
        evaluation.setLogEntry(savedLog);
        evaluation.setSupervisor(supervisor); // Assumes Evaluation.java was updated to User type
        evaluation.setRemarks(request.getSupervisorComment());
        evaluation.setScore(request.getScore()); // Ensure LogReviewRequest has a score field
        evaluationRepository.save(evaluation);

        // 3. Notify Student
        notificationService.notify(
                student,
                "Log Update: Your entry for " + log.getLogDate() + " has been " + request.getStatus()
        );

        return savedLog;
    }
}