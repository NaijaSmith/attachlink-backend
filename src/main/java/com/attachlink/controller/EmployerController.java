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
package com.attachlink.controller;

import com.attachlink.entity.User;
import com.attachlink.service.EmployerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST Controller for Employer operations.
 * Mirrors SupervisorController functionality for employer-assigned students.
 */
@RestController
@RequestMapping("/api/employer")
@PreAuthorize("hasAuthority('EMPLOYER')")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    /**
     * Get students assigned to this employer (dashboard).
     */
    @GetMapping("/my-students")
    public ResponseEntity<List<User>> getAssignedStudents(Principal principal) {
        return ResponseEntity.ok(
                employerService.getAssignedStudents(principal.getName())
        );
    }
}
