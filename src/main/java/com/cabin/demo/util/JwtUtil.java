package com.cabin.demo.util;

import com.cabin.demo.dto.AuthenticatedUser;
import com.cabin.demo.services.UserService;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.express.config.Environment;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class JwtUtil {
    private static final String JWT_SECRET = Environment.getString("JWT_SECRET");
    private static final long EXPIRATION_MS = 86400000; // 1 day
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(JWT_SECRET)
    );


    public static String generateToken(Long userId, String email) {
        String subj = IdObfuscator.encodeUserId(userId);
        return Jwts.builder()
                .setSubject(subj)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    public static AuthenticatedUser parseAndValidateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            long id = IdObfuscator.decodeUserId(claims.getSubject());
            String email = claims.get("email", String.class);
            return new AuthenticatedUser(id, email);
        } catch (JwtException e) {
            // includes SignatureException, ExpiredJwtException, etc.
            log.error("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String token = generateToken(1L, "sang@gmail.com");
        System.out.println("Generated Token: " + token);
        AuthenticatedUser authenticatedUser = parseAndValidateToken(token);
        if (authenticatedUser != null) {
            System.out.println("Authenticated User ID: " + authenticatedUser.getUserId());
            System.out.println("Authenticated User Email: " + authenticatedUser.getEmail());
        } else {
            System.out.println("Failed to parse token");
        }
    }
}