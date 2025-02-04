package com.cabin.express.datasource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        String configPath = Paths.get(System.getProperty("user.home"), ".m2", "cabin", "settings.properties").toString();
        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration from " + configPath, e);
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
