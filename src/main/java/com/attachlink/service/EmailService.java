/**
 * 1. ADD THIS TO YOUR application.properties
 * -----------------------------------------
 * spring.mail.host=sandbox.smtp.mailtrap.io
 * spring.mail.port=2525
 * spring.mail.username=e0eaf05450c9f1
 * spring.mail.password=YOUR_FULL_PASSWORD_FROM_SCREENSHOT
 * spring.mail.properties.mail.smtp.auth=true
 * spring.mail.properties.mail.smtp.starttls.enable=true
 */

package com.attachlink.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a simple text-based email.
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@yourdomain.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }

    /**
     * Sends a rich HTML email (Common for professional apps).
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("app@yourdomain.com");
        helper.setTo(to);
        helper.setSubject(subject);
        // Set 'true' to indicate the content is HTML
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
