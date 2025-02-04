package com.cabin.express.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {

        loadConfig();

        String dbUrl = properties.getProperty("DB_URL", "jdbc:mysql://localhost:3306/cabin");
        String dbUser = properties.getProperty("DB_USER", "root");
        String dbPassword = properties.getProperty("DB_PASSWORD", "root");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new RuntimeException("Missing required database environment variables!");
        }

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);

        ds = new HikariDataSource(config);
    }


    private static void loadConfig() {
        String configPath = Paths.get(System.getProperty("user.home"), ".m2", "cabin", "settings.properties").toString();

        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration from " + configPath, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
