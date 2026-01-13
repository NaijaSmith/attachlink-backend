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
     * Supervisor can APPROVE (REVIEWED) or REJECT
     */
    public LogEntry reviewLog(Long logId, LogReviewRequest request) {

        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() ->
                        new RuntimeException("Log entry not found"));

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
