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

import com.attachlink.dto.LogReviewRequest;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupervisorService {

    private final LogEntryRepository logEntryRepository;
    private final NotificationService notificationService;

    public SupervisorService(LogEntryRepository logEntryRepository,
                             NotificationService notificationService) {
        this.logEntryRepository = logEntryRepository;
        this.notificationService = notificationService;
    }

    /**
     * Fetch all log entries that are awaiting supervisor review
     */
    public List<LogEntry> getSubmittedLogs() {
        return logEntryRepository.findByStatus(LogStatus.SUBMITTED);
    }

    /**
     * Review a specific log entry
     * Supervisor can APPROVE or REJECT
     */
    public LogEntry reviewLog(Long logId, LogReviewRequest request) {

        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() ->
                        new RuntimeException("Log entry not found"));

        // Validate that log is in SUBMITTED status
        if (!LogStatus.SUBMITTED.equals(log.getStatus())) {
            throw new RuntimeException("Log entry is not in SUBMITTED status for review");
        }

        // Validate status is either APPROVED or REJECTED
        if (request.getStatus() == null || 
            (!LogStatus.APPROVED.equals(request.getStatus()) && 
             !LogStatus.REJECTED.equals(request.getStatus()))) {
            throw new RuntimeException("Invalid status. Must be APPROVED or REJECTED");
        }

        // Update log review details
        log.setStatus(request.getStatus());
        log.setSupervisorComment(request.getSupervisorComment());
        log.setReviewedAt(LocalDateTime.now());

        LogEntry savedLog = logEntryRepository.save(log);

        //  Notify the student
        notificationService.notify(
                log.getStudent(),
                "Your log dated " + log.getLogDate() +
                " has been " + request.getStatus()
        );

        return savedLog;
    }
}