package com.cabin.demo.dao;

import com.cabin.demo.entity.auth.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserDao extends BaseDAO<User> {

    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    public User findByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }
}
