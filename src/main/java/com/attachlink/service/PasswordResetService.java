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

import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service handling password recovery logic including OTP generation,
 * email dispatch, and secure password updating.
 */
@Service
public class PasswordResetService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Temporary storage for OTPs. 
    // Optimization Note: In a scaled production environment, replace this with 
    // a dedicated 'PasswordResetToken' database table or Redis.
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    public PasswordResetService(EmailService emailService, 
                                UserRepository userRepository, 
                                PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static class OtpData {
        String code;
        LocalDateTime expiry;

        OtpData(String code) {
            this.code = code;
            this.expiry = LocalDateTime.now().plusMinutes(10);
        }
    }

    /**
     * Generates a 6-digit OTP, stores it temporarily, and emails the user.
     * * @param email The user's registered email address.
     * @throws MessagingException If the email fails to send.
     */
    public void processForgotPassword(String email) throws MessagingException {
        // Ensure the user exists before proceeding
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found with that email address."));

        // Generate a 6-digit random code
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Store it with expiration
        otpStorage.put(email, new OtpData(otp));

        // Construct HTML content
        String htmlContent = """
            <div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 8px;'>
                <h2 style='color: #2c3e50;'>Password Reset Request</h2>
                <p>We received a request to reset your password for your AttachLink account.</p>
                <p>Use the following One-Time Password (OTP) to proceed. This code is valid for <b>10 minutes</b>:</p>
                <div style='background: #f4f7f6; padding: 15px; border-radius: 4px; text-align: center;'>
                    <h1 style='color: #2c3e50; letter-spacing: 10px; margin: 0;'>%s</h1>
                </div>
                <p style='margin-top: 20px; font-size: 0.9em; color: #7f8c8d;'>
                    If you did not request this, please ignore this email or contact support if you have concerns.
                </p>
            </div>
            """.formatted(otp);

        emailService.sendHtmlEmail(email, "Your Password Reset OTP", htmlContent);
    }

    /**
     * Verifies the provided OTP and updates the user's password in the database.
     *
     * @param email       The user's email.
     * @param receivedOtp The OTP provided by the user.
     * @param newPassword The plain-text new password to be hashed.
     * @return true if successful, false otherwise.
     */
    @Transactional
    public boolean verifyAndResetPassword(String email, String receivedOtp, String newPassword) {
        OtpData data = otpStorage.get(email);

        if (data == null) return false;

        // Check for expiration
        if (data.expiry.isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            return false;
        }

        // Verify code match
        if (data.code.equals(receivedOtp)) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                // Securely hash the new password
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                
                otpStorage.remove(email);
                return true;
            }
        }

        return false;
    }
}