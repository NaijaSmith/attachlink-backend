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

import com.attachlink.entity.Notification;
import com.attachlink.entity.User;
import com.attachlink.repository.NotificationRepository;
import com.attachlink.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    public NotificationService(NotificationRepository notificationRepository, 
                               UserRepository userRepository,
                               FcmService fcmService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    /**
     * Saves or updates the FCM device token for a user.
     * Fixes "method saveDeviceToken(User, String) is undefined" in AuthController.java
     */
    @Transactional
    public void saveDeviceToken(User user, String token) {
        if (user != null && token != null) {
            user.setFcmToken(token);
            userRepository.save(user);
        }
    }

    /**
     * Creates a persistent notification and attempts a push delivery.
     */
    @Transactional
    public void notify(User user, String message) {
        if (user == null) return;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setReadStatus(false);
        notificationRepository.save(notification);

        // ✅ Push notification if token exists
        if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
            try {
                fcmService.sendPush(user.getFcmToken(), "AttachLink Update", message);
            } catch (Exception e) {
                // Log error but don't fail the transaction if push fails
                System.err.println("Failed to send push notification: " + e.getMessage());
            }
        }
    }

    /**
     * Fetches all notifications for a specific user, ordered by newest first.
     */
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Marks a specific notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }
}