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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service handling the lifecycle of a Log Entry.
 * Refined to match Controller requirements for 'createLog' and 'updateLogStatus'.
 */
@Service
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;
    private final NotificationService notificationService;

    public LogEntryService(LogEntryRepository logEntryRepository, 
                           NotificationService notificationService) {
        this.logEntryRepository = logEntryRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates and submits a new log entry.
     * This matches the call in LogEntryController.submitLog().
     */
    @Transactional
    public LogEntry createLog(LogEntryRequest request, User student) {
        LogEntry log = new LogEntry();
        log.setStudent(student);
        log.setLogDate(request.getLogDate());
        log.setActivities(request.getActivities());
        log.setChallenges(request.getChallenges());
        log.setLearningOutcomes(request.getLearningOutcomes());
        
        // Since the user is clicking "Submit", we set status to SUBMITTED immediately
        log.setStatus(LogStatus.SUBMITTED);
        log.setCreatedAt(LocalDateTime.now());

        LogEntry savedLog = logEntryRepository.save(log);

        // Notify the supervisor if the student has one assigned
        if (student.getSupervisor() != null) {
            notificationService.notify(
                student.getSupervisor(),
                "New Log Submission: " + student.getFullName() + " has submitted a log for " + log.getLogDate()
            );
        }

        return savedLog;
    }

    /**
     * Updates the status of a log entry.
     * Used by the Supervisor via the PatchMapping in the Controller.
     */
    @Transactional
    public LogEntry updateLogStatus(Long logId, LogStatus status) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log entry not found with ID: " + logId));
        
        log.setStatus(status);
        log.setReviewedAt(LocalDateTime.now());
        
        return logEntryRepository.save(log);
    }

    /**
     * Retrieves all logs for a specific student.
     */
    public List<LogEntry> getStudentLogs(User student) {
        return logEntryRepository.findByStudent(student);
    }

    @Transactional
    public void deleteLog(Long logId, User student) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        
        if (!log.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized delete attempt.");
        }
        
        logEntryRepository.delete(log);
    }
}