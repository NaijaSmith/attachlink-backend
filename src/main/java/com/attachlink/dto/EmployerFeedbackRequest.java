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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for capturing detailed employer feedback on student performance.
 * Includes categorical ratings for professional competencies.
 */
public class EmployerFeedbackRequest {
     
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @Min(value = 1, message = "General rating must be at least 1")
    @Max(value = 5, message = "General rating cannot exceed 5")
    private int overallRating;

    @Min(value = 1, message = "Punctuality rating must be at least 1")
    @Max(value = 5, message = "Punctuality rating cannot exceed 5")
    private int punctuality;

    @Min(value = 1, message = "Technical skills rating must be at least 1")
    @Max(value = 5, message = "Technical skills rating cannot exceed 5")
    private int technicalSkills;

    @Min(value = 1, message = "Communication rating must be at least 1")
    @Max(value = 5, message = "Communication rating cannot exceed 5")
    private int communication;

    @Min(value = 1, message = "Teamwork rating must be at least 1")
    @Max(value = 5, message = "Teamwork rating cannot exceed 5")
    private int teamworkRating;

    @NotBlank(message = "Qualitative feedback comment is required")
    @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
    private String comment;

    // Default constructor for serialization
    public EmployerFeedbackRequest() {}

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public int getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(int punctuality) {
        this.punctuality = punctuality;
    }

    public int getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(int technicalSkills) {
        this.technicalSkills = technicalSkills;
    }

    public int getCommunication() {
        return communication;
    }

    public void setCommunication(int communication) {
        this.communication = communication;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTeamworkRating() {
        return teamworkRating;
    }

    public void setTeamworkRating(int teamworkRating) {
        this.teamworkRating = teamworkRating;
    }
}
