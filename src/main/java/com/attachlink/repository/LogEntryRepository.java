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
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for LogEntry entity.
 * Provides abstracted data access for internship daily logs and supervisor dashboard metrics.
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Retrieves all log entries for a specific student, ordered by date descending.
     * Useful for displaying the log history in chronological order.
     */
    List<LogEntry> findByStudentOrderByLogDateDesc(User student);

    /**
     * Filters log entries by their current status (e.g., SUBMITTED, APPROVED, REJECTED).
     */
    List<LogEntry> findByStatus(LogStatus status);

    /**
     * Counts total log entries submitted by a student.
     * Used for student progress statistics.
     */
    long countByStudent(User student);

    /**
     * Counts log entries for a student based on status.
     * Useful for checking how many logs are still pending review.
     */
    long countByStudentAndStatus(User student, LogStatus status);

    /**
     * Retrieves log entries for a student that have received supervisor feedback.
     */
    List<LogEntry> findByStudentAndSupervisorCommentIsNotNull(User student);

    /**
     * Retrieves all log entries belonging to students assigned to a specific supervisor, 
     * filtered by status (e.g., logs awaiting review by that specific supervisor).
     */
    List<LogEntry> findAllByStudent_SupervisorAndStatus(User supervisor, LogStatus status);

    /**
     * Counts log entries across all assigned students for a supervisor based on status.
     * Core requirement for Supervisor Dashboard "Pending Review" metrics.
     * Uses underscore navigation to reach the supervisor field within the student entity.
     */
    long countByStudent_SupervisorAndStatus(User supervisor, LogStatus status);

    /**
     * Checks if a log entry already exists for a student on a specific date.
     * Prevents duplicate submissions for the same day.
     */
    boolean existsByStudentAndLogDate(User student, LocalDate logDate);
}