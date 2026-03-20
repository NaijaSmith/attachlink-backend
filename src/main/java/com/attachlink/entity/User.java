/*
 * Copyright 2026 Nicholas Kariuki Wambui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.attachlink.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "registration_number")
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
     * Safely retrieves registration number or default value.
     */
    public String getRegistrationNumberDisplay() {
        return registrationNumber != null ? registrationNumber : "N/A";
    }

    /**
     * Returns the first part of the full name for UI greetings.
     */
    @JsonProperty("firstName")
    public String getFirstName() {
        if (fullName == null || fullName.trim().isEmpty()) return "User";
        return fullName.trim().split("\\s+")[0];
    }

    /**
     * Checks if the user has an associated institution.
     */
    public boolean hasValidInstitution() {
        return institutionName != null && !institutionName.trim().isEmpty();
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