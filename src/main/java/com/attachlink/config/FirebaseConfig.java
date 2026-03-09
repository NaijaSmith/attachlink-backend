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
package com.attachlink.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class to initialize Firebase Admin SDK.
 * Optimized for both local development and production environments.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private static final String SERVICE_ACCOUNT_PATH = "firebase-service-account.json";

    @PostConstruct
    public void initFirebase() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase Admin SDK was already initialized.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(getCredentials())
                    .build();

            FirebaseApp.initializeApp(options);
            logger.info("Firebase Admin SDK has been initialized successfully.");
            
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: Service account file error. Path: {}", SERVICE_ACCOUNT_PATH);
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase initialization: {}", e.getMessage());
        }
    }

    /**
     * Logic to load credentials. 
     * Prioritizes the JSON file in resources, but could be extended to 
     * check environment variables for Docker/Cloud deployments.
     */
    private GoogleCredentials getCredentials() throws IOException {
        ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_PATH);
        
        if (!resource.exists()) {
            // Fallback: Check if credentials are provided via GOOGLE_APPLICATION_CREDENTIALS env var
            logger.warn("Firebase JSON not found in resources. Attempting to use default Google Credentials.");
            return GoogleCredentials.getApplicationDefault();
        }

        try (InputStream serviceAccount = resource.getInputStream()) {
            return GoogleCredentials.fromStream(serviceAccount);
        }
    }
}