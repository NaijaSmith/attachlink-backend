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

import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Handles database interactions for Students, Supervisors, and Employers.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     * Essential for the login process and JWT generation.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if an email exists to prevent duplicate registrations.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves all active users with a specific role.
     * Used to populate selection lists (e.g., choosing a Supervisor) in the Android app.
     */
    List<User> findByRoleAndActiveTrue(String role);

    /**
     * Retrieves all students currently assigned to a specific supervisor.
     * Used in the Supervisor's "My Students" list.
     */
    List<User> findAllBySupervisorAndRole(User supervisor, String role);

    /**
     * Retrieves all students currently assigned to a specific industry employer.
     */
    List<User> findAllByEmployerAndRole(User employer, String role);

    /**
     * Counts active students for a supervisor's dashboard statistics.
     */
    long countBySupervisorAndActiveTrue(User supervisor);

    /**
     * Counts active students for an employer's dashboard statistics.
     */
    long countByEmployerAndActiveTrue(User employer);
    
    /**
     * Global search functionality for administrative or look-up purposes.
     * Supports partial matches for names or registration numbers.
     */
    List<User> findByFullNameContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCase(
            String fullName, 
            String registrationNumber
    );

    /**
     * Finds a user by ID and ensures they match a specific role.
     * Useful for validating that a selected supervisorId actually belongs to a SUPERVISOR.
     */
    Optional<User> findByIdAndRole(Long id, String role);

    /**
     * Finds a user by ID and ensures they match a specific role and are active.
     * Adds an extra layer of validation for registration and assignment processes.
     */
    Optional<User> findByIdAndRoleAndActiveTrue(Long id, String role);

    /**
     * Retrieves all supervisors for administrative purposes.
     */
    List<User> findByRole(String role);

    List<User> findAllBySupervisor(User supervisor);

    List<User> findAllByEmployer(User employer);


}