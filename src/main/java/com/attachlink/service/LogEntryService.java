package com.attachlink.service;

import com.attachlink.dto.LogEntryRequest;
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

    // Create new log entry
    public LogEntry createLog(LogEntryRequest request, User student) {

        LogEntry log = new LogEntry();
        log.setLogDate(request.getLogDate());
        log.setDescription(request.getDescription());
        log.setEvidenceUrl(request.getEvidenceUrl());

        // Default system values
        log.setStatus(LogStatus.SUBMITTED);
        log.setSubmittedAt(LocalDateTime.now());
        log.setStudent(student);

        return logEntryRepository.save(log);
    }

    // Get logs for logged-in student
    public List<LogEntry> getStudentLogs(User student) {
        return logEntryRepository.findByStudent(student);
    }
}
