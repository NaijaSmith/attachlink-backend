package com.attachlink.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; 

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "registration_number", length = 20)
    private String registrationNumber;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;
    
    private boolean active = true;
}