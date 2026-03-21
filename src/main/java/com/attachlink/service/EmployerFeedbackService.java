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

import com.attachlink.dto.EmployerFeedbackRequest;
import com.attachlink.entity.EmployerFeedback;
import com.attachlink.entity.User;
import com.attachlink.entity.Roles;
import com.attachlink.repository.EmployerFeedbackRepository;
import com.attachlink.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for managing comprehensive employer feedback on student performance.
 * Handles calculation of overall performance metrics and role-based validation.
 */
@Service
public class EmployerFeedbackService {

    private final EmployerFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public EmployerFeedbackService(
            EmployerFeedbackRepository feedbackRepository,
            UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    /**
     * Submits multi-category feedback for a student from an employer.
     * Automatically calculates the overall mean rating based on individual skill categories.
     */
    @Transactional
    public EmployerFeedback submitFeedback(
            EmployerFeedbackRequest request,
            User employer) {

        // Validate that the reviewer is actually an employer
        if (employer.getRole() != Roles.EMPLOYER) {
            throw new IllegalArgumentException("Unauthorized: Only employers can submit performance feedback.");
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + request.getStudentId()));

        // Validate that the recipient is a student
        if (student.getRole() != Roles.STUDENT) {
            throw new IllegalArgumentException("Invalid Target: Feedback can only be submitted for student profiles.");
        }

        EmployerFeedback feedback = new EmployerFeedback();
        feedback.setEmployer(employer);
        feedback.setStudent(student);
        
        // Map individual professional categories from the request
        feedback.setTechnicalSkillsRating(request.getTechnicalSkills());
        feedback.setCommunicationRating(request.getCommunication());
        feedback.setPunctualityRating(request.getPunctuality());
        feedback.setTeamworkRating(request.getTeamworkRating());
        
        // Calculate and set the overall mean rating
        double overall = (request.getTechnicalSkills() + 
                          request.getCommunication() + 
                          request.getPunctuality() + 
                          request.getTeamworkRating()) / 4.0;
        
        feedback.setOverallRating((int) overall);
        feedback.setComment(request.getComment());
        feedback.setSubmittedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    /**
     * Retrieves all feedback entries for a specific student.
     */
    @Transactional(readOnly = true)
    public List<EmployerFeedback> getFeedbackForStudent(User student) {
        if (student.getRole() != Roles.STUDENT) {
            throw new IllegalArgumentException("The requested user profile does not contain student metrics.");
        }
        return feedbackRepository.findByStudent(student);
    }

    /**
     * Retrieves a summarized skill breakdown for a student's profile analytics.
     */
    @Transactional(readOnly = true)
    public Map<String, Double> getStudentPerformanceAnalytics(User student) {
        return feedbackRepository.getDetailedMetricsForStudent(student);
    }

    /**
     * Retrieves feedback history submitted by a specific employer.
     */
    @Transactional(readOnly = true)
    public List<EmployerFeedback> getFeedbackByEmployer(User employer) {
        return feedbackRepository.findByEmployer(employer);
    }
}