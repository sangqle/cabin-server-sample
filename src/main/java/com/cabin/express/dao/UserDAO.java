package com.cabin.express.dao;

import com.cabin.express.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import static com.cabin.express.datasource.HibernateUtil.getSessionFactory;

public class UserDAO extends BaseDAO<User> {

    public UserDAO(SessionFactory sessionFactory) {
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
