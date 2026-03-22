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

package com.attachlink.security;

import com.attachlink.entity.User;
import com.attachlink.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for loading user-specific data during authentication.
 * Refined to support both hasRole() and hasAuthority() by granting dual authorities.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Maps the database User entity to a Spring Security UserDetails object.
     * Grants dual authorities (Raw + ROLE_ prefix) to fix 403 Forbidden issues.
     */
    private UserDetails createSpringSecurityUser(User user) {
        String rawRole = (user.getRole() != null) ? user.getRole().trim().toUpperCase() : "STUDENT";

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 1. Add raw authority (e.g., "SUPERVISOR") for .hasAuthority()
        authorities.add(new SimpleGrantedAuthority(rawRole));

        // 2. Add prefixed authority (e.g., "ROLE_SUPERVISOR") for .hasRole()
        if (!rawRole.startsWith("ROLE_")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rawRole));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                // Link account status to your Entity's 'active' field
                .disabled(!user.isActive()) 
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}