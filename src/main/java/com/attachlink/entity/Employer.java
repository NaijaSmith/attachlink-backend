package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String contactPerson;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
