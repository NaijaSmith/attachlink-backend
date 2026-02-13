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
package com.attachlink.controller;

import com.attachlink.dto.RegisterRequest;
import com.attachlink.security.JwtUtil;
import com.attachlink.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;
import com.attachlink.dto.UserMeResponse;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserRepository userRepository;



    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          AuthService authService,
                          UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.userRepository = userRepository;   
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email,
                                        @RequestParam String password) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(token);
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    // GET CURRENT USER INFO
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
    }   

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    UserMeResponse res = new UserMeResponse(
            user.getEmail(),
            user.getRole(),
            user.getFullName(),
            user.getRegistrationNumber(),
            user.getInstitutionName()
    );

    return ResponseEntity.ok(res);
}


}
