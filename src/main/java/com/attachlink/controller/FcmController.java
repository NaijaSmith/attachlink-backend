package com.attachlink.controller;

import com.attachlink.dto.FcmTokenRequest;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
public class FcmController {

    private final UserRepository userRepository;

    public FcmController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/token")
    public ResponseEntity<?> saveToken(@RequestBody FcmTokenRequest request,
                                       Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        user.setFcmToken(request.getToken());
        userRepository.save(user);

        return ResponseEntity.ok("FCM token saved");
    }
}
