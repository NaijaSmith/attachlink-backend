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

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     * Used during authentication and profile retrieval.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if an email is already registered in the system.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves all students assigned to a specific supervisor.
     * Required for SupervisorService.getAssignedStudents().
     */
    List<User> findAllBySupervisor(User supervisor);

    /**
     * Counts the total number of students assigned to a supervisor.
     * Required for SupervisorService.getDashboardStats().
     */
    long countBySupervisor(User supervisor);
}