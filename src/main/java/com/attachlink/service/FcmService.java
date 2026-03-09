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

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service to handle Firebase Cloud Messaging (FCM) operations.
 * Includes support for individual tokens and topic-based notifications.
 */
@Service
public class FcmService {

    private static final Logger logger = LoggerFactory.getLogger(FcmService.class);

    /**
     * Sends a push notification to a specific device token.
     * Annotated with @Async to prevent push delivery latency from affecting business logic.
     *
     * @param token Target device FCM token
     * @param title Notification title
     * @param body  Notification body content
     */
    @Async
    public void sendPush(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            logger.warn("Attempted to send push to null or empty token. Skipping.");
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("FCM delivery failed for token {}: {}", token, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during FCM delivery: {}", e.getMessage());
        }
    }

    /**
     * Sends a push notification to a specific topic (e.g., "all_students" or "new_jobs").
     *
     * @param topic Target topic name
     * @param title Notification title
     * @param body  Notification body content
     * @param data  Optional key-value pairs for deep linking or app logic
     */
    @Async
    public void sendTopicPush(String topic, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            FirebaseMessaging.getInstance().send(messageBuilder.build());
            logger.info("Successfully sent topic message to: {}", topic);
        } catch (Exception e) {
            logger.error("FCM topic send failed for {}: {}", topic, e.getMessage());
        }
    }
}