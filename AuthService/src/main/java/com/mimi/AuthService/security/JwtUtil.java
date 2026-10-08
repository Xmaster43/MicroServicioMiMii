package com.mimi.AuthService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtUtil {

    @Value("${JWT_SECRET:supersecreto-supersecreto-supersecreto-123456}")
    private String secretKey;
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h

    private SecretKey getKey() {
        // Clave HMAC válida para HS256
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Genera token con email + rol
    public String generateToken(String email, Long rolId) {
        return Jwts.builder()
                .subject(email)                 // subject = email
                .claim("rol", rolId)            // claim extra = rol
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey(), Jwts.SIG.HS256)   // <-- IMPORTANTE
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long extractRole(String token) {
        return getClaims(token).get("rol", Long.class);
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("Token inválido: " + e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(getKey())   // API moderna de jjwt 0.12.5
                .build();

        return parser.parseSignedClaims(token).getPayload();
    }
}
