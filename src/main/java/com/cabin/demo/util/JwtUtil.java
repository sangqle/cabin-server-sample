package com.cabin.demo.util;

import com.cabin.express.config.Environment;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String JWT_SECRET = Environment.getString("JWT_SECRET");
    private static final long EXPIRATION_MS = 86400000; // 1 day

    public static String generateToken(String userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}