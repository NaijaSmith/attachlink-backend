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
package com.attachlink.repository;

import com.attachlink.entity.EmployerFeedback;
import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for EmployerFeedback entity providing data access operations
 * and specialized statistical queries for student performance metrics.
 */
@Repository
public interface EmployerFeedbackRepository extends JpaRepository<EmployerFeedback, Long> {

    /**
     * Retrieves all feedback entries given to a specific student.
     */
    List<EmployerFeedback> findByStudent(User student);

    /**
     * Retrieves all feedback entries provided by a specific employer.
     */
    List<EmployerFeedback> findByEmployer(User employer);

    /**
     * Calculates the mean overall rating for a student across all feedback.
     * Note: References 'overallRating' from the updated entity.
     */
    @Query("SELECT AVG(e.overallRating) FROM EmployerFeedback e WHERE e.student = :student")
    Double getAverageRatingForStudent(@Param("student") User student);

    /**
     * Retrieves a breakdown of average scores across all professional categories for a student.
     * This is useful for generating radar charts or skill breakdowns on the student profile.
     */
    @Query("SELECT " +
           "AVG(e.technicalSkillsRating) as avgTechnical, " +
           "AVG(e.communicationRating) as avgCommunication, " +
           "AVG(e.punctualityRating) as avgPunctuality, " +
           "AVG(e.teamworkRating) as avgTeamwork " +
           "FROM EmployerFeedback e WHERE e.student = :student")
    Map<String, Double> getDetailedMetricsForStudent(@Param("student") User student);
}