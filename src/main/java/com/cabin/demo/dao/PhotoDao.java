package com.cabin.demo.dao;

import com.cabin.demo.entity.photo.Photo;
import org.hibernate.SessionFactory;
import org.hibernate.query.Order;

import java.util.List;

public class PhotoDao  extends BaseDAO<Photo>  {
    public PhotoDao(SessionFactory sessionFactory) {
        super(sessionFactory, Photo.class);
    }

    public List<Photo> getSlicePhotoByUserId(Long userId, int offset, int limit) {
        try (var session = getSessionFactory().openSession()) {
            return session.createQuery("FROM Photo WHERE user.id = :userId ORDER BY id DESC", Photo.class)
                    .setParameter("userId", userId)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
        } catch (Exception e) {
            return null;
        }
    }
}
