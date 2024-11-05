package com.example.crudtask.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private String secretKey = "your-secret-key-really-long-at-least-256-bits-long-xyz"; // Пример длинного ключа

    public String generateToken(String username) {
        byte[] keyBytes = secretKey.getBytes();
        var key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public Claims extractClaims(String token) {
        byte[] keyBytes = secretKey.getBytes();
        var key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public byte[] decodeSecretKey(String encodedSecret) {
        return Decoders.BASE64URL.decode(encodedSecret);
    }

    public String encodeSecretKey(byte[] key) {
        return Encoders.BASE64URL.encode(key);
    }
}
