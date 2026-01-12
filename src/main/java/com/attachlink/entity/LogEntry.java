package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private LocalDate logDate;

    @Column(length = 1000)
    private String description;

    private String evidenceUrl;

    private String status;
    // SUBMITTED, REVIEWED, REJECTED

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
