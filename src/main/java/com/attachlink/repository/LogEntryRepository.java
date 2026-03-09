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

import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Retrieves all log entries for a specific student.
     */
    List<LogEntry> findByStudent(User student);

    /**
     * Filters log entries by their current status (PENDING, APPROVED, etc.).
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
     * Retrieves log entries for a student that have received supervisor feedback.
     */
    List<LogEntry> findByStudentAndSupervisorCommentIsNotNull(User student);

    /**
     * Retrieves all log entries belonging to students assigned to a specific supervisor, 
     * filtered by status (e.g., PENDING logs for review).
     */
    List<LogEntry> findAllByStudentSupervisorAndStatus(User supervisor, LogStatus status);

    /**
     * Counts log entries across all assigned students for a supervisor based on status.
     * Required for SupervisorService dashboard statistics.
     */
    long countByStudentSupervisorAndStatus(User supervisor, LogStatus status);
}