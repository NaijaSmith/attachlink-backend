package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employer_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    @Column(length = 1000)
    private String comments;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;
}
