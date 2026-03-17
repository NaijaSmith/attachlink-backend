package com.attachlink.service;

import com.attachlink.dto.RegisterRequest;
import com.attachlink.entity.Student;
import com.attachlink.entity.Supervisor;
import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service handling user authentication and registration logic.
 * Fixed: Explicitly handling the persistence of profile entities to avoid 500 errors.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_ROLES = Set.of("STUDENT", "SUPERVISOR", "EMPLOYER", "ADMIN");

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    @Transactional
    public User register(RegisterRequest request) {
        // 1. Validate Role
        String role = (request.getRole() == null) 
                ? null 
                : request.getRole().trim().toUpperCase();

        if (role == null || !ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role. Allowed roles: " + ALLOWED_ROLES);
        }

        // 2. Validate and Normalize Email
        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Email is required.");
        }
        
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        // 3. Structural Validation (Fat DTO check)
        validateRegistrationData(role, request);

        // 4. Build User Entity
        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .fullName(request.getFullName().trim())
                .institutionName(request.getInstitutionName().trim())
                .active(true)
                .build();

        // 5. Handle Foreign Key Assignments (Supervisor/Employer)
        // If these IDs are invalid, findById will return empty and avoid 500 FK errors
        handleAssignments(user, request);

        // 6. Save User First to generate ID (Crucial for linked tables)
        User savedUser = userRepository.save(user);

        // 7. Create Profiles and link to savedUser
        if ("STUDENT".equals(role)) {
            createStudentProfile(savedUser, request);
        } else if ("SUPERVISOR".equals(role)) {
            createSupervisorProfile(savedUser, request);
        }

        // 8. Final Save (Updates user with profile links)
        return userRepository.save(savedUser);
    }

    private void createStudentProfile(User user, RegisterRequest request) {
        Student student = new Student();
        student.setUser(user); // Link to the user with the generated ID
        student.setRegistrationNumber(request.getRegistrationNumber().trim());
        student.setCourse(request.getCourse().trim());
        user.setStudentProfile(student);
        // Note: Ensure User entity has @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    }

    private void createSupervisorProfile(User user, RegisterRequest request) {
        Supervisor supervisor = new Supervisor();
        supervisor.setUser(user);
        user.setSupervisorProfile(supervisor);
    }

private void handleAssignments(User user, RegisterRequest request) {
    // Handle Supervisor Assignment
    if (request.getSupervisorId() != null && request.getSupervisorId() > 0) {
        userRepository.findById(request.getSupervisorId())
            .ifPresent(supervisor -> {
                // Matches the renamed field: private User supervisor;
                user.setSupervisor(supervisor);
            });
    }

    // Handle Employer Assignment
    if (request.getEmployerId() != null && request.getEmployerId() > 0) {
        userRepository.findById(request.getEmployerId())
            .ifPresent(employer -> {
                // Matches the renamed field: private User employer;
                user.setEmployer(employer);
            });
    }
}

    private void validateRegistrationData(String role, RegisterRequest request) {
        if (isBlank(request.getFullName())) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (isBlank(request.getInstitutionName())) {
            throw new IllegalArgumentException("Institution name is required.");
        }
        if ("STUDENT".equals(role)) {
            if (isBlank(request.getRegistrationNumber()) || isBlank(request.getCourse())) {
                throw new IllegalArgumentException("Students must provide Registration Number and Course.");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}