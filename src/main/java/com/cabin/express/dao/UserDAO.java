package com.cabin.express.dao;

import com.cabin.express.datasource.HibernateUtil;
import com.cabin.express.entity.User;
import com.cabin.express.loggger.CabinLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserDAO {

    /**
     * Insert a new user into the database.
     * @param user The user object to insert.
     * @return true if successful, false otherwise.
     */
    public boolean insertUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            CabinLogger.error("Error inserting user: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Update an existing user in the database.
     * @param user The user object with updated details.
     * @return true if successful, false otherwise.
     */
    public boolean updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            CabinLogger.error("Error updating user: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Retrieve all users from the database.
     * @return A list of User objects.
     */
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            CabinLogger.error("Error retrieving users: " + e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Find a user by ID.
     * @param userId The ID of the user.
     * @return The user object if found, otherwise null.
     */
    public User getUserById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, userId);
        } catch (Exception e) {
            CabinLogger.error("Error finding user: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Delete a user by ID.
     * @param userId The ID of the user to delete.
     * @return true if deletion is successful, false otherwise.
     */
    public boolean deleteUser(int userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            CabinLogger.error("Error deleting user: " + e.getMessage(), e);
        }
        return false;
    }
}
