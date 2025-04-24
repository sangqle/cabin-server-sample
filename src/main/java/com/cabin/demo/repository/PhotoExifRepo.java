package com.cabin.demo.repository;

import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.entity.photo.PhotoExif;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class PhotoExifRepo {
    private final SessionFactory sessionFactory;

    public static final PhotoExifRepo INSTANCE = new PhotoExifRepo();

    private PhotoExifRepo() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public PhotoExif savePhotoExif(PhotoExif photoExif) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(photoExif);
            transaction.commit();
            return photoExif;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public PhotoExif getPhotoExifById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(PhotoExif.class, id);
        }
    }

    public List<PhotoExif> getAllPhotoExifs() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from PhotoExif", PhotoExif.class).list();
        }
    }

    public PhotoExif updatePhotoExif(PhotoExif photoExif) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(photoExif);
            transaction.commit();
            return photoExif;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public void deletePhotoExif(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            PhotoExif photoExif = session.get(PhotoExif.class, id);
            if (photoExif != null) {
                session.delete(photoExif);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
