/*
 * Copyright (c) 2026 Nicholas Kariuki. All rights reserved.
 */
package com.attachlink.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Core User entity for AttachLink.
 * Handles authentication and acts as a root for Student/Supervisor profiles.
 */
@Entity
@Table(name = "users")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // STUDENT, SUPERVISOR, EMPLOYER, ADMIN

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // --- RELATIONSHIPS TO SPECIFIC TABLES ---

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Student studentProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Supervisor supervisorProfile;

    // --- SELF-REFERENCING LINKS (Hierarchy Management) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    @JsonIgnore 
    private User assignedSupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    @JsonIgnore
    private User assignedEmployer;

    /**
     * Proxies the registration number from the Student profile.
     */
    public String getRegistrationNumber() {
        return (studentProfile != null) ? studentProfile.getRegistrationNumber() : "N/A";
    }

    /**
     * Proxies the assigned supervisor.
     */
    public User getSupervisor() {
        return assignedSupervisor;
    }

    /**
     * Extracts the first name from the full name for greetings and file exports.
     */
    public String getFirstName() {
        if (fullName == null || fullName.trim().isEmpty()) return "User";
        return fullName.trim().split("\\s+")[0];
    }

    // --- ROLE CHECKS ---

    public boolean isStudent() { 
        return "STUDENT".equalsIgnoreCase(this.role); 
    }
    
    public boolean isSupervisor() { 
        return "SUPERVISOR".equalsIgnoreCase(this.role); 
    }
    
    public boolean isEmployer() { 
        return "EMPLOYER".equalsIgnoreCase(this.role); 
    }
    
    public boolean isAdmin() { 
        return "ADMIN".equalsIgnoreCase(this.role); 
    }
}