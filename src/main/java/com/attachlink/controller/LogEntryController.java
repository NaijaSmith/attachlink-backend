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
package com.attachlink.controller;

import com.attachlink.dto.LogEntryRequest;
import com.attachlink.entity.User;
import com.attachlink.service.LogEntryService;
import com.attachlink.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogEntryController {

    private final LogEntryService logEntryService;
    private final UserRepository userRepository;

    public LogEntryController(LogEntryService logEntryService,
                              UserRepository userRepository) {
        this.logEntryService = logEntryService;
        this.userRepository = userRepository;
    }

    // Submit log
    @PostMapping
    public ResponseEntity<?> submitLog(
            @RequestBody LogEntryRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow();

        return ResponseEntity.ok(
                logEntryService.createLog(request, student)
        );
    }

    // View my logs
    @GetMapping
    public ResponseEntity<List<?>> getMyLogs(Authentication authentication) {

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow();

        return ResponseEntity.ok(
                logEntryService.getStudentLogs(student)
        );
    }
}
