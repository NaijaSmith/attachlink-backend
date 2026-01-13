package com.attachlink.repository;

import com.attachlink.entity.EmployerFeedback;
import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployerFeedbackRepository
            extends JpaRepository<EmployerFeedback, Long> {

    List<EmployerFeedback> findByStudent(User student);

    List<EmployerFeedback> findByEmployer(User employer);
    
}
