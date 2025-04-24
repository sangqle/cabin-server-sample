package com.cabin.demo.services;

import com.cabin.demo.datasource.DatabaseConfig;
import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.entity.photo.PhotoExif;
import com.cabin.demo.repository.PhotoRepo;
import com.cabin.demo.util.ExifData;
import com.cabin.demo.util.ExifUtil;
import com.cabin.express.http.UploadedFile;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);

    private PhotoService() {
    }

    public static final PhotoService INSTANCE = new PhotoService();

    public long savePhoto(User user, UploadedFile file) throws Exception {
        long photoId = 0;
        byte[] content = file.getContent();
        ExifData exifData = ExifUtil.getExifData(content);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Save Photo
            Photo photo = new Photo();
            photo.setUser(user);
            photo.setTitle(file.getFileName());
            photo.setDescription(file.getFileName());
            photo.setShootingAt(exifData.getShootingTime());
            photo.setUrl("https://image.truyenquan.com/DSC09067.jpg");
            session.save(photo);

            // Save EXIF Data
            PhotoExif photoExif = new PhotoExif();
            photoExif.setPhoto(photo); // Assuming a relationship is set
            photoExif.setExifRaw(exifData.getExifEntry());
            photoExif.setCameraModel(exifData.getCameraModel());
            photoExif.setLensModel(exifData.getLensModel());
            photoExif.setIso(exifData.getIso());
            session.save(photoExif);

            transaction.commit();
            photoId = photo.getId();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Error saving photo: {}", e.getMessage());
        } finally {
            session.close();
        }
        return photoId;
    }
}
