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

import com.attachlink.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Verification entity.
 * Handles database operations for student verification records.
 */
@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    
    /**
     * Finds all verification records associated with a specific user account.
     * Changed from findByStudentId to findByUserId to match the VerificationService logic.
     */
    List<Verification> findByUserId(Long userId);

    /**
     * Checks if a user has any active verification records.
     * Changed from studentId to userId.
     */
    boolean existsByUserIdAndVerifiedTrue(Long userId);
}