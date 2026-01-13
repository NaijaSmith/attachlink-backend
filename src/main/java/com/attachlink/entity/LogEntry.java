package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date the log refers to (e.g., Day 5 of attachment)
    @Column(nullable = false)
    private LocalDate logDate;

    // What the student did on that day
    @Column(nullable = false, length = 1500)
    private String description;

    // Optional link to uploaded evidence (PDF, image, etc.)
    @Column(length = 500)
    private String evidenceUrl;

    // SUBMITTED, REVIEWED, REJECTED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogStatus status;

    // Supervisor comment after review
    @Column(length = 1000)
    private String supervisorComment;

    // When the log was submitted
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // When the supervisor reviewed the log
    private LocalDateTime reviewedAt;

    // Many logs belong to one student (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;


}
