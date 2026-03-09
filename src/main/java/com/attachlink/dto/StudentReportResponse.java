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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for comprehensive student performance reports.
 * Extends basic analytics by including student identification and supervisor feedback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportResponse {

    private Long studentId;
    
    private String studentName;
    
    private String registrationNumber;

    private long totalLogs;
    
    private long approvedLogs;
    
    private long rejectedLogs;
    
    /**
     * Calculated percentage of approved logs
     */
    private double approvalRate;

    /**
     * Aggregated rating from all industrial attachment evaluations
     */
    private double averageEmployerRating;
    
    /**
     * Collection of feedback comments provided by academic or industry supervisors
     */
    private List<String> supervisorComments;
}