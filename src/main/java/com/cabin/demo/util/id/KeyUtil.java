package com.cabin.demo.util.id;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Utility to derive a 64-bit key from a passphrase.
 */
public class KeyUtil {

    /**
     * Hashes the passphrase with SHA-256 and truncates to 8 bytes.
     * @param passphrase any non-null string
     * @return a reproducible long key
     */
    public static long deriveKey64(String passphrase) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");             // SHA-256 algorithm :contentReference[oaicite:3]{index=3}
            byte[] hash = sha256.digest(passphrase.getBytes(StandardCharsets.UTF_8));
            long key = 0;
            // take first 8 bytes (big-endian)
            for (int i = 0; i < 8; i++) {
                key = (key << 8) | (hash[i] & 0xFFL);
            }
            return key;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to derive encryption key", e);
        }
    }

    public static SecretKeySpec deriveAesKeySpec(String passphrase) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(passphrase.getBytes(StandardCharsets.UTF_8));
            byte[] keyBytes = Arrays.copyOf(hash, 16);  // AES-128 requires 16 bytes :contentReference[oaicite:7]{index=7}
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to derive AES key", e);
        }
    }

}
