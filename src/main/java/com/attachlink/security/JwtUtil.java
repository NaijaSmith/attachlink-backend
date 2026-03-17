/*
 * Copyright 2026 Nicholas Kariuki Wambui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.attachlink.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${attachlink.jwt.secret}")
    private String secretKeyString;

    private final long expiration = 86400000; // 1 day
    private SecretKey cachedKey;

    @PostConstruct
    protected void init() {
        if (secretKeyString == null || secretKeyString.trim().isEmpty()) {
            throw new IllegalStateException("JWT Secret Key string is missing! Set ATTACHLINK_JWT_SECRET in .env");
        }
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.cachedKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Defensive getter to ensure the key is never null.
     */
    private SecretKey getSigningKey() {
        if (this.cachedKey == null) {
            init(); // Manual fallback if PostConstruct wasn't called yet
        }
        return this.cachedKey;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Uses the safe getter
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) &amp;&amp; !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
