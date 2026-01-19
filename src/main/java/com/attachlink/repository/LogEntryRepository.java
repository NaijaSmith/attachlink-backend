package com.attachlink.repository;

import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByStudent(User student);

    List<LogEntry> findByStatus(LogStatus status);

    long countByStudent(User student);

    long countByStudentAndStatus(User student, LogStatus status);

    List<LogEntry> findByStudentAndSupervisorCommentIsNotNull(User student);
}
