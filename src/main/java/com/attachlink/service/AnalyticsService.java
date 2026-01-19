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
