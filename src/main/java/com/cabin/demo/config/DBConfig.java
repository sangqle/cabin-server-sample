package com.cabin.demo.config;

import com.cabin.express.config.Environment;
import lombok.Builder;
import lombok.Data;

/**
 * Configuration for Database.
 */
@Data
@Builder
public class DBConfig {
    /** Database URL */
    private final String url;

    /** Database User */
    private final String user;

    /** Database Password */
    private final String password;

    /**
     * Loads DBConfig from environment variables:
     *   DB_URL, DB_USER, DB_PASSWORD.
     */
    public static DBConfig fromEnv() {
        String url = Environment.getString("DB_URL");
        String user = Environment.getString("DB_USER");
        String password = Environment.getString("DB_PASSWORD");

        return DBConfig.builder()
                .url(url)
                .user(user)
                .password(password)
                .build();
    }

    /**
     * Ensures all required values are present.
     * Call this before using the config to fail fast on startup.
     */
    public void validate() {
        if (url == null || url.isBlank() ||
                user == null || user.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Missing Database configuration: " +
                            "DB_URL, DB_USER, and DB_PASSWORD are required."
            );
        }
    }
}