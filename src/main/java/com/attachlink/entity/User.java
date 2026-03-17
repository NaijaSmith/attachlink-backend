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
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Student studentProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Supervisor supervisorProfile;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = true)
    @JsonIgnore 
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = true)
    @JsonIgnore
    private User employer;

    // Helper method for Registration Number
    public String getRegistrationNumber() {
        return (studentProfile != null) ? studentProfile.getRegistrationNumber() : "N/A";
    }

    // Helper method for Greeting UI
    public String getFirstName() {
        if (fullName == null || fullName.trim().isEmpty()) return "User";
        return fullName.trim().split("\\s+")[0];
    }
    

    public boolean hasValidInstitution() {
        return institutionName != null && !institutionName.trim().isEmpty();
    }
}