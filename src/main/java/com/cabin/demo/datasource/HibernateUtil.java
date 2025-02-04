package com.cabin.demo.datasource;

import com.cabin.demo.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

            // Add annotated classes
            configuration.addAnnotatedClass(User.class);

            // Dynamically set DB properties from DatabaseConfig
            Properties dbProperties = new Properties();
            dbProperties.setProperty("hibernate.connection.url", DatabaseConfig.getDbUrl());
            dbProperties.setProperty("hibernate.connection.username", DatabaseConfig.getDbUser());
            dbProperties.setProperty("hibernate.connection.password", DatabaseConfig.getDbPassword());
            configuration.setProperties(dbProperties);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}