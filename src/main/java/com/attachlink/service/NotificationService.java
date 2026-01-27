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
