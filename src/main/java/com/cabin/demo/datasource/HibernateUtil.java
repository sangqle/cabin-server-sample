package com.cabin.demo.datasource;

import com.cabin.demo.config.DBConfig;
import com.cabin.demo.locator.ServiceLocator;
import com.cabin.demo.entity.album.Album;
import com.cabin.demo.entity.album.AlbumPhoto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.auth.UserAuth;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.entity.photo.PhotoExif;
import com.cabin.demo.entity.photo.PhotoTag;
import com.cabin.demo.entity.photo.Tag;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // 1) Retrieve DBConfig from ServiceLocator
            DBConfig dbConfig = ServiceLocator.get(DBConfig.class);

            // 2) Configure HikariCP
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("org.postgresql.Driver"); // Assuming PostgreSQL
            hikariConfig.setJdbcUrl(dbConfig.getUrl());
            hikariConfig.setUsername(dbConfig.getUser());
            hikariConfig.setPassword(dbConfig.getPassword());

            // (optional tuning)
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setConnectionTestQuery("SELECT 1");
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);

            // 3) Build the StandardServiceRegistry
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect") // Assuming PostgreSQL
                    .applySetting("hibernate.hbm2ddl.auto", "update")
                    .applySetting("hibernate.show_sql", "true")
                    .applySetting("hibernate.format_sql", "true")
                    .applySetting("hibernate.connection.datasource", dataSource)
                    .applySetting("hibernate.transaction.coordinator_class", "jdbc")
                    .build();

            // 4) Add your entities
            MetadataSources sources = new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Photo.class)
                    .addAnnotatedClass(PhotoExif.class)
                    .addAnnotatedClass(Tag.class)
                    .addAnnotatedClass(PhotoTag.class)
                    .addAnnotatedClass(Album.class)
                    .addAnnotatedClass(AlbumPhoto.class)
                    .addAnnotatedClass(UserAuth.class);

            Metadata metadata = sources.getMetadataBuilder().build();

            // 5) Build SessionFactory
            return metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}