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

import com.attachlink.dto.EmployerFeedbackRequest;
import com.attachlink.entity.EmployerFeedback;
import com.attachlink.entity.User;
import com.attachlink.repository.EmployerFeedbackRepository;
import com.attachlink.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    // Submit feedback
    public EmployerFeedback submitFeedback(
            EmployerFeedbackRequest request,
            User employer) {

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        EmployerFeedback feedback = new EmployerFeedback();
        feedback.setEmployer(employer);
        feedback.setStudent(student);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setSubmittedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    // View feedback for a student
    public List<EmployerFeedback> getFeedbackForStudent(User student) {
        return feedbackRepository.findByStudent(student);
    }
}
        