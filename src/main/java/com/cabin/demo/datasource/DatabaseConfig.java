package com.cabin.demo.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    private static synchronized void loadConfig() {
        if (loaded) return;  // only once
        final String configFile = "application.properties";
        log.info("Loading configuration file: {}", configFile);

        try (InputStream input = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream(configFile)) {

            if (input == null) {
                throw new FileNotFoundException(
                        "Configuration file '" + configFile + "' not found in classpath");
            }

            properties.load(input);
            loaded = true;
            log.info("Configuration loaded successfully");

        } catch (FileNotFoundException e) {
            log.error("Config load failed: file not found", e);
            throw new IllegalStateException("Missing configuration file: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Config load failed: I/O error", e);
            throw new IllegalStateException("Error reading configuration file", e);
        }
    }

    // Public getters now ensure the config is loaded (or fail early)
    public static String getDbUrl() {
        loadConfig();
        return properties.getProperty("DB_URL", "jdbc:mysql://localhost:3306/cabin");
    }

    public static String getDbUser() {
        loadConfig();
        return properties.getProperty("DB_USER", "root");
    }

    public static String getDbPassword() {
        loadConfig();
        return properties.getProperty("DB_PASSWORD", "root");
    }

    public static String getDriverClass() {
        loadConfig();
        String drv = properties.getProperty("DB_DRIVER_CLASS", "org.postgresql.Driver");
        log.info("Using JDBC driver: {}", drv);
        return drv;
    }

    public static String getDialect() {
        loadConfig();
        return properties.getProperty(
                "DB_DIALECT",
                "org.hibernate.dialect.PostgreSQL95Dialect"
        );
    }
}
