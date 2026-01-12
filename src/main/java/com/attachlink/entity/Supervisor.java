package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supervisors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supervisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String department;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
