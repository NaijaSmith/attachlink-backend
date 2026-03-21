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
package com.attachlink.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration.
 * Optimized to support both snake_case (Android) and camelCase (Web/Java) 
 * using @JsonAlias to prevent validation "Required" errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * Role can be 'STUDENT', 'SUPERVISOR', or 'EMPLOYER'
     */
    @NotBlank(message = "User role is required")
    private String role;

    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    @JsonAlias("fullName") 
    private String fullName;

    @JsonProperty("registration_number")
    @JsonAlias("registrationNumber")
    private String registrationNumber;

    @JsonProperty("institution")
    @JsonAlias("institutionName")
    private String institutionName;

    private String course;

    /**
     * ID of the selected Academic Supervisor (Mandatory for Student role)
     */
    @JsonProperty("supervisor_id")
    @JsonAlias("supervisorId")
    private Long supervisorId;

    /**
     * ID of the selected Industry Employer (Mandatory for Student role)
     */
    @JsonProperty("employer_id")
    @JsonAlias("employerId")
    private Long employerId;

    /**
     * Helper method to validate student-specific requirements.
     */
    public boolean isValidForStudent() {
        if ("STUDENT".equalsIgnoreCase(this.role)) {
            return supervisorId != null && employerId != null;
        }
        return true;
    }
}