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

import com.attachlink.dto.StudentReportResponse;
import com.attachlink.entity.LogStatus;
import com.attachlink.entity.User;
import com.attachlink.repository.EmployerFeedbackRepository;
import com.attachlink.repository.LogEntryRepository;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final LogEntryRepository logEntryRepository;
    private final EmployerFeedbackRepository feedbackRepository;

    public ReportService(
            LogEntryRepository logEntryRepository,
            EmployerFeedbackRepository feedbackRepository) {
        this.logEntryRepository = logEntryRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public StudentReportResponse generateStudentReport(User student) {

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

        var comments =
                logEntryRepository
                        .findByStudentAndSupervisorCommentIsNotNull(student)
                        .stream()
                        .map(log -> log.getSupervisorComment())
                        .collect(Collectors.toList());

        StudentReportResponse report =
                new StudentReportResponse();

        report.setStudentId(student.getId());
        report.setStudentName(student.getFullName());
        report.setRegistrationNumber(student.getRegistrationNumber());
        report.setTotalLogs(totalLogs);
        report.setApprovedLogs(approvedLogs);
        report.setRejectedLogs(rejectedLogs);
        report.setApprovalRate(approvalRate);
        report.setAverageEmployerRating(
                avgRating == null ? 0 : avgRating);
        report.setSupervisorComments(comments);

        return report;
    }
}
