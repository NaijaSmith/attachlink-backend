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

import com.attachlink.entity.User;
import com.attachlink.entity.Verification;

import com.attachlink.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class to handle logic related to User Verifications.
 * Integrated with AuthService to ensure operations are performed on the correct authenticated user.
 */
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final AuthService authService;

    /**
     * Verifies the currently authenticated user.
     * Fixed: Uses the implementation now provided in AuthService.
     */
    @Transactional
    public Verification verifyCurrentStudent() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        
        Verification verification = new Verification();
        verification.setUser(currentUser);
        verification.setVerified(true);
        verification.setVerificationDate(LocalDate.now());

        return verificationRepository.save(verification);
    }

    /**
     * Retrieves all verification records for the currently logged-in user.
     */
    public List<Verification> getMyVerifications() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return verificationRepository.findByUserId(currentUser.getId());
    }

    /**
     * Checks if the current user has any verified records.
     */
    public boolean isCurrentUserVerified() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return verificationRepository.existsByUserIdAndVerifiedTrue(currentUser.getId());
    }
}