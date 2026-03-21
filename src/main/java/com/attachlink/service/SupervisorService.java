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
 * Optimized for secure ID comparison and metadata-rich log retrieval.
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

    public List<User> getAssignedStudents(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));
        return userRepository.findAllBySupervisor(supervisor);
    }

    /**
     * Fetches pending logs. 
     * Hibernate will include the Student entity (and thus Name/RegNo) 
     * because of the LogEntry entity relationships.
     */
    public List<LogEntry> getPendingLogsForSupervisor(String email) {
        User supervisor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supervisor account not found"));

        return logEntryRepository.findAllByStudent_SupervisorAndStatusOrderByUpdatedAtDesc(supervisor, LogStatus.SUBMITTED);
    }

    /**
     * Review a specific log entry.
     * Fixed: Permission error by using explicit ID comparison to bypass Hibernate proxy issues.
     */
    @Transactional
    public LogEntry reviewLog(Long logId, LogReviewRequest request, String supervisorEmail) {
        User supervisor = userRepository.findByEmail(supervisorEmail)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        LogEntry log = logEntryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log entry not found with ID: " + logId));

        User student = log.getStudent();
        User assignedSupervisor = student.getSupervisor();
        if (assignedSupervisor == null || 
            assignedSupervisor.getId().longValue() != supervisor.getId().longValue()) {
            
            System.out.println("MISMATCH: Log is for Supervisor ID [" + 
                (assignedSupervisor != null ? assignedSupervisor.getId() : "NULL") + 
                "] but you are logged in as ID [" + supervisor.getId() + "]");
                
            throw new SecurityException("Permission denied.");
        }

        if (!LogStatus.SUBMITTED.equals(log.getStatus())) {
            throw new IllegalStateException("Log entry is already in " + log.getStatus() + " status.");
        }

        // 1. Update Log
        log.setStatus(request.getStatus());
        log.setReviewedAt(LocalDateTime.now());
        LogEntry savedLog = logEntryRepository.save(log);

        // 2. Create Evaluation (remarks maps to supervisor_comment via DTO)
        Evaluation evaluation = Evaluation.builder()
                .logEntry(savedLog)
                .supervisor(supervisor)
                .remarks(request.getSupervisorComment())
                .score(request.getScore() != null ? request.getScore() : 0)
                .submittedAt(LocalDateTime.now())
                .build();
        
        evaluationRepository.save(evaluation);

        // 3. Notification
        String message = String.format("Your log for %s has been %s by %s.", 
                log.getLogDate(), 
                request.getStatus().toString().toLowerCase(),
                supervisor.getFullName());
                
        notificationService.notify(student, message);

        return savedLog;
    }
}