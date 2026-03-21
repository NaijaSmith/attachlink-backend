/*
 * Copyright 2026 Nicholas Kariuki Wambui
 */
package com.attachlink.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing detailed feedback provided by an Employer regarding a Student's performance.
 */
@Entity
@Table(name = "employer_feedbacks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // --- Detailed Metrics ---

    @Min(1) @Max(5)
    @Column(name = "technical_skills_rating", nullable = false)
    private int technicalSkillsRating;

    @Min(1) @Max(5)
    @Column(name = "communication_rating", nullable = false)
    private int communicationRating;

    @Min(1) @Max(5)
    @Column(name = "punctuality_rating", nullable = false)
    private int punctualityRating;

    @Min(1) @Max(5)
    @Column(name = "teamwork_rating", nullable = false)
    private int teamworkRating;

    /**
     * Calculated or overall aggregate rating.
     */
    @Min(1) @Max(5)
    @Column(name = "overall_rating", nullable = false)
    private int overallRating;

    // --- Qualitative Feedback ---

    @NotBlank(message = "Detailed comments are required")
    @Size(max = 3000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}