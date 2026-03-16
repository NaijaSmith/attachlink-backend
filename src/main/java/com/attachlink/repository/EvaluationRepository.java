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
package com.attachlink.repository;

import com.attachlink.entity.Evaluation;
import com.attachlink.entity.LogEntry;
import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // Find evaluation for a specific log
    Optional<Evaluation> findByLogEntry(LogEntry logEntry);

    // Fetch all evaluations for logs belonging to a specific student
    @Query("SELECT e FROM Evaluation e WHERE e.logEntry.student = :student")
    List<Evaluation> findAllByStudent(@Param("student") User student);
}