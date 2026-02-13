/*Copyright 2026 Nicholas Kariuki Wambui

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.attachlink.service;

import com.attachlink.dto.StudentAnalyticsResponse;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.EmployerFeedbackRepository;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

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

    public StudentAnalyticsResponse getStudentAnalytics(User student) {

        long totalLogs = logEntryRepository.countByStudent(student);
        long approvedLogs =
                logEntryRepository.countByStudentAndStatus(
                        student, LogStatus.APPROVED);
        long rejectedLogs =
                logEntryRepository.countByStudentAndStatus(
                        student, LogStatus.REJECTED);

        double approvalRate = totalLogs == 0
                ? 0
                : (double) approvedLogs / totalLogs * 100;

        Double avgRating =
                feedbackRepository.getAverageRatingForStudent(student);

        StudentAnalyticsResponse response =
                new StudentAnalyticsResponse();

        response.setStudentId(student.getId());
        response.setTotalLogs(totalLogs);
        response.setApprovedLogs(approvedLogs);
        response.setRejectedLogs(rejectedLogs);
        response.setApprovalRate(approvalRate);
        response.setAverageEmployerRating(
                avgRating == null ? 0 : avgRating
        );

        return response;
    }
}
