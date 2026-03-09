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