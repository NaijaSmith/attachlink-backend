package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    @Column(length = 1000)
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "log_entry_id", nullable = false)
    private LogEntry logEntry;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Supervisor supervisor;
}
