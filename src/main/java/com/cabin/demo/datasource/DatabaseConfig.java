package com.cabin.demo.datasource;

import java.io.IOException;
import java.util.Properties;

import java.io.InputStream;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Failed to load application.properties from classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database configuration from application.properties", e);
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("DB_URL", "jdbc:mysql://localhost:3306/cabin");
    }

    public static String getDbUser() {
        return properties.getProperty("DB_USER", "root");
    }

    public static String getDbPassword() {
        return properties.getProperty("DB_PASSWORD", "root");
    }
}
