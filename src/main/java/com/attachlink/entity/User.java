package com.attachlink.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

/**
 * User Entity representing Students, Supervisors, and Employers.
 * Updated with @JsonAlias to bridge the gap between snake_case (Android) 
 * and camelCase (Spring Boot) during registration and profile updates.
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonAlias({"full_name", "fullName"}) // Support both conventions
    private String fullName;

    @Column(name = "institution_name")
    @JsonAlias({"institution_name", "institutionName", "institution"})
    private String institutionName;

    @Column(name = "fcm_token")
    @JsonAlias("fcm_token")
    private String fcmToken;

    @Column(name = "registration_number")
    @JsonAlias({"registration_number", "registrationNumber"})
    private String registrationNumber;

    @Column(name = "course")
    private String course;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // --- Self-Referencing Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = true)
    @JsonIgnore 
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = true)
    @JsonIgnore
    private User employer;

    /**
     * Logic for UI greetings.
     */
    @JsonProperty("firstName")
    public String getFirstName() {
        if (fullName == null || fullName.trim().isEmpty()) return "User";
        return fullName.trim().split("\\s+")[0];
    }

    // Expose names for JSON without circular references
    @JsonProperty("supervisorName")
    public String getSupervisorName() {
        return supervisor != null ? supervisor.getFullName() : null;
    }

    @JsonProperty("employerName")
    public String getEmployerName() {
        return employer != null ? employer.getFullName() : null;
    }
}