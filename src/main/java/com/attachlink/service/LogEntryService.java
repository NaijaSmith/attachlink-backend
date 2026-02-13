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
import com.attachlink.dto.LogReviewRequest;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;

    public LogEntryService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    // --- STUDENT ACTIONS ---

    public LogEntry createLog(LogEntryRequest request, User student) {
        LogEntry log = new LogEntry();
        log.setLogDate(request.getLogDate());
        log.setDescription(request.getDescription());
        log.setEvidenceUrl(request.getEvidenceUrl());

        log.setStatus(LogStatus.SUBMITTED);
        log.setSubmittedAt(LocalDateTime.now());
        log.setStudent(student);

        return logEntryRepository.save(log);
    }

    public List<LogEntry> getStudentLogs(User student) {
        return logEntryRepository.findByStudent(student);
    }

    // --- SUPERVISOR ACTIONS ---

    // Get all logs that have been SUBMITTED but not yet reviewed
    public List<LogEntry> getLogsForReview() {
        return logEntryRepository.findByStatus(LogStatus.SUBMITTED);
    }

    public LogEntry reviewLog(Long logId, LogReviewRequest request) {
        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log entry not found with ID: " + logId));

        // Update status (e.g., REVIEWED or REJECTED)
        log.setStatus(request.getStatus()); 
        log.setSupervisorComment(request.getSupervisorComment());
        log.setReviewedAt(LocalDateTime.now());

        return logEntryRepository.save(log);
    }
}