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
package com.attachlink.repository;

import com.attachlink.entity.LogEntry;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LogEntry entity.
 * Refined to support chronological priority for resubmitted logs.
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Retrieves all log entries for a specific student, ordered by date descending.
     */
    List<LogEntry> findByStudentOrderByLogDateDesc(User student);

    /**
     * Finds a specific log for a student by date.
     */
    Optional<LogEntry> findByStudentAndLogDate(User student, LocalDate logDate);

    /**
     * Filters log entries by their current status globally.
     */
    List<LogEntry> findByStatus(LogStatus status);

    /**
     * Counts total log entries submitted by a student.
     */
    long countByStudent(User student);

    /**
     * Counts log entries for a student based on status.
     */
    long countByStudentAndStatus(User student, LogStatus status);

    /**
     * REFINED: Retrieves all log entries for a supervisor's students, 
     * ordered by UpdatedAt Descending. This ensures RESUBMITTED logs 
     * appear at the top of the dashboard immediately.
     */
    List<LogEntry> findAllByStudent_SupervisorAndStatusOrderByUpdatedAtDesc(User supervisor, LogStatus status);

    /**
     * Counts log entries across all assigned students for a supervisor based on status.
     */
    long countByStudent_SupervisorAndStatus(User supervisor, LogStatus status);

    /**
     * Checks if a log entry already exists for a student on a specific date.
     */
    boolean existsByStudentAndLogDate(User student, LocalDate logDate);

    /**
     * Custom query to find logs within a specific date range for a student.
     */
    @Query("SELECT l FROM LogEntry l WHERE l.student = :student AND l.logDate BETWEEN :startDate AND :endDate ORDER BY l.logDate ASC")
    List<LogEntry> findLogsInDateRange(@Param("student") User student, 
                                      @Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
}
