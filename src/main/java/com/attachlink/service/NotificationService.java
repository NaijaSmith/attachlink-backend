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
package com.attachlink.service;

import com.attachlink.entity.Notification;
import com.attachlink.entity.User;
import com.attachlink.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private FcmService fcmService;

    public NotificationService(NotificationRepository notificationRepository, FcmService fcmService) {
        this.notificationRepository = notificationRepository;
        this.fcmService = fcmService;
    }

    // Create notification
public void notify(User user, String message) {

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setMessage(message);
    notification.setCreatedAt(LocalDateTime.now());
    notification.setReadStatus(false);
    notificationRepository.save(notification);

    // ✅ Push notification if token exists
    if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
        fcmService.sendPush(user.getFcmToken(),
                "AttachLink Update", message);
    }
}


    // Get user notifications
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
