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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating internship log entries.
 * Refined to match LogEntryService requirements for activities, challenges, and outcomes.
 */
public class LogEntryRequest {

    @NotNull(message = "Log date is required")
    @PastOrPresent(message = "Log date cannot be in the future")
    private LocalDate logDate;

    @NotBlank(message = "Activities description is required")
    private String activities;

    @NotBlank(message = "Please describe the challenges faced")
    private String challenges;

    @NotBlank(message = "Please state the learning outcomes")
    private String learningOutcomes;

    /**
     * Default constructor for JSON deserialization.
     */
    public LogEntryRequest() {
    }

    /**
     * Full constructor for easy instantiation in services or tests.
     */
    public LogEntryRequest(LocalDate logDate, String activities, String challenges, String learningOutcomes) {
        this.logDate = logDate;
        this.activities = activities;
        this.challenges = challenges;
        this.learningOutcomes = learningOutcomes;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getChallenges() {
        return challenges;
    }

    public void setChallenges(String challenges) {
        this.challenges = challenges;
    }

    public String getLearningOutcomes() {
        return learningOutcomes;
    }

    public void setLearningOutcomes(String learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }
}