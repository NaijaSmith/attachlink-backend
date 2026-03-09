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
package com.attachlink.service;

import com.attachlink.dto.StudentAnalyticsResponse;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.EmployerFeedbackRepository;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for calculating and retrieving analytics for students.
 */
@Service
public class AnalyticsService {

    private final LogEntryRepository logEntryRepository;
    private final EmployerFeedbackRepository feedbackRepository;

    public AnalyticsService(
            LogEntryRepository logEntryRepository,
            EmployerFeedbackRepository feedbackRepository) {
        this.logEntryRepository = logEntryRepository;
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Calculates analytics data for a specific student.
     * * @param student The user entity representing the student.
     * @return A DTO containing calculated log statistics and employer feedback ratings.
     */
    @Transactional(readOnly = true)
    public StudentAnalyticsResponse getStudentAnalytics(User student) {

        // 1. Fetch raw counts from the database
        long totalLogs = logEntryRepository.countByStudent(student);
        long approvedLogs = logEntryRepository.countByStudentAndStatus(student, LogStatus.APPROVED);
        long rejectedLogs = logEntryRepository.countByStudentAndStatus(student, LogStatus.REJECTED);

        // 2. Calculate approval rate (with division-by-zero protection)
        double rawApprovalRate = totalLogs == 0
                ? 0.0
                : (double) approvedLogs / totalLogs * 100;

        // Round to 2 decimal places for better UI presentation
        double approvalRate = Math.round(rawApprovalRate * 100.0) / 100.0;

        // 3. Fetch average rating (handle potential null from database)
        Double avgRating = feedbackRepository.getAverageRatingForStudent(student);
        double finalAvgRating = (avgRating == null) ? 0.0 : avgRating;

        // 4. Construct and populate the response DTO
        StudentAnalyticsResponse response = new StudentAnalyticsResponse();
        response.setStudentId(student.getId());
        response.setTotalLogs(totalLogs);
        response.setApprovedLogs(approvedLogs);
        response.setRejectedLogs(rejectedLogs);
        response.setApprovalRate(approvalRate);
        response.setAverageEmployerRating(finalAvgRating);

        return response;
    }
}