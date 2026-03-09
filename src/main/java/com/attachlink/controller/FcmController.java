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

import com.attachlink.dto.FcmTokenRequest;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Controller for managing Firebase Cloud Messaging (FCM) tokens.
 * These tokens allow the system to send push notifications to specific user devices.
 */
@RestController
@RequestMapping("/api/fcm")
public class FcmController {

    private final UserRepository userRepository;

    public FcmController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Updates the FCM registration token for the currently authenticated user.
     * * @param request The object containing the new FCM token.
     * @param authentication The current security context.
     * @return A response entity confirming the update.
     */
    @PostMapping("/token")
    public ResponseEntity<?> saveToken(@RequestBody FcmTokenRequest request,
                                     Authentication authentication) {
        
        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "FCM token cannot be empty"));
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));

            // Only update and save if the token has actually changed to save DB cycles
            if (!request.getToken().equals(user.getFcmToken())) {
                user.setFcmToken(request.getToken());
                userRepository.save(user);
            }

            return ResponseEntity.ok(Map.of("message", "FCM token updated successfully"));

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save FCM token"));
        }
    }

    /**
     * Removes the FCM registration token for the currently authenticated user.
     * Useful during logout to stop notifications to the current device.
     * * @param authentication The current security context.
     * @return A response entity confirming the token was removed.
     */
    @DeleteMapping("/token")
    public ResponseEntity<?> removeToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));

            // Clear the token
            user.setFcmToken(null);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "FCM token removed successfully"));

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to remove FCM token"));
        }
    }
}