package com.attachlink.controller;

import com.attachlink.dto.RegisterRequest;
import com.attachlink.security.JwtUtil;
import com.attachlink.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          AuthService authService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
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
}
