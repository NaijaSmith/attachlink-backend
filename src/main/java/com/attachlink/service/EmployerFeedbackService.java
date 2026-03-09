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

/**
 * Service for managing employer feedback on student performance.
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
     * Submits feedback for a student from an employer.
     * Includes role validation to ensure data integrity.
     */
    @Transactional
    public EmployerFeedback submitFeedback(
            EmployerFeedbackRequest request,
            User employer) {

        // Validate that the reviewer is actually an employer
        if (employer.getRole() != Roles.EMPLOYER) {
            throw new IllegalArgumentException("Only employers can submit feedback.");
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + request.getStudentId()));

        // Validate that the recipient is a student
        if (student.getRole() != Roles.STUDENT) {
            throw new IllegalArgumentException("Feedback can only be submitted for students.");
        }

        EmployerFeedback feedback = new EmployerFeedback();
        feedback.setEmployer(employer);
        feedback.setStudent(student);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setSubmittedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    /**
     * Retrieves all feedback entries for a specific student.
     */
    public List<EmployerFeedback> getFeedbackForStudent(User student) {
        if (student.getRole() != Roles.STUDENT) {
            throw new IllegalArgumentException("The requested user is not a student.");
        }
        return feedbackRepository.findByStudent(student);
    }

    /**
     * Retrieves feedback submitted by a specific employer.
     */
    public List<EmployerFeedback> getFeedbackByEmployer(User employer) {
        return feedbackRepository.findByEmployer(employer);
    }
}