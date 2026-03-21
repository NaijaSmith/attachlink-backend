/*
 * Copyright 2026 Nicholas Kariuki Wambui
 *
 * Licensed under the Apache License, Version 2.0 (the \"License\");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by required by law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.attachlink.service;

import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service handling business logic for Employers.
 * Manages assigned students and dashboard statistics.
 */
@Service
public class EmployerService {

    private final UserRepository userRepository;

    public EmployerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves list of students assigned to this employer.
     */
    public List<User> getAssignedStudents(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        return userRepository.findAllByEmployer(employer);
    }
}
