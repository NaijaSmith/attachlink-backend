package com.attachlink.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("firebase-service-account.json");

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Firebase init failed: " + e.getMessage());
        }
    }
}
