package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationNumber;
    private String course;
    private String institution;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
