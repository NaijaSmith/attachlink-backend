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
package com.attachlink.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Entity representing an evaluation performed by a Supervisor on a specific Student Log Entry.
 */
@Entity
@Table(name = "evaluations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numerical score for the specific log entry (e.g., scale of 1-100).
     */
    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 100, message = "Score cannot exceed 100")
    @Column(nullable = false)
    private int score;

    /**
     * Qualitative remarks or feedback from the supervisor regarding the work logged.
     */
    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /**
     * The specific log entry being evaluated.
     * Maps to log_entry_id in the database.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_entry_id", nullable = false, unique = true)
    private LogEntry logEntry;

    /**
     * The supervisor who performed this evaluation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", referencedColumnName = "id")
    private User supervisor;

    /**
     * Timestamp of when the evaluation was first recorded.
     * Managed automatically by Hibernate.
     */
    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    /**
     * Timestamp of the last update to this evaluation.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}