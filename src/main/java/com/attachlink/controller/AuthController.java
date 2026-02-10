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
