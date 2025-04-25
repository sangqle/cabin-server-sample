package com.cabin.demo.repository;

import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.entity.photo.Photo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PhotoRepository {

    private final SessionFactory sessionFactory;
    public static final PhotoRepository INSTANCE = new PhotoRepository();

    public PhotoRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Photo savePhoto(Photo photo) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(photo);
            transaction.commit();
            return photo;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public Photo getPhotoById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Photo.class, id);
        }
    }

    public List<Photo> getAllPhotos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Photo", Photo.class).list();
        }
    }

    public Photo updatePhoto(Photo photo) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(photo);
            transaction.commit();
            return photo;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public void deletePhoto(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Photo photo = session.get(Photo.class, id);
            if (photo != null) {
                session.delete(photo);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}