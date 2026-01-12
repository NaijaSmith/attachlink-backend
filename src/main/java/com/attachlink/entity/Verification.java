package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean verified;

    private LocalDate verificationDate;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
