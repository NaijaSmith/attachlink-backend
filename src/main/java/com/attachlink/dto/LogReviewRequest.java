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

import com.attachlink.entity.LogStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for supervisors to review student log entries.
 * Updated with Jackson annotations to support both camelCase and snake_case (Android).
 */
public class LogReviewRequest {

    @NotNull(message = "Review status (APPROVED/REJECTED) is mandatory")
    private LogStatus status;

    @Size(max = 500, message = "Supervisor comments must not exceed 500 characters")
    @JsonProperty("supervisor_comment") // Primary name for Android/JSON
    @JsonAlias("supervisorComment")      // Supports camelCase for Web/Java
    private String supervisorComment;

    @Min(value = 0, message = "Score cannot be less than 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;

    public LogReviewRequest() {
    }

    public LogReviewRequest(LogStatus status, String supervisorComment, Integer score) {
        this.status = status;
        this.supervisorComment = supervisorComment;
        this.score = score;
    }

    // Getters and Setters with explicit JsonProperty to ensure correct mapping
    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    @JsonProperty("supervisor_comment")
    public String getSupervisorComment() {
        return supervisorComment;
    }

    @JsonProperty("supervisor_comment")
    public void setSupervisorComment(String supervisorComment) {
        this.supervisorComment = supervisorComment;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}