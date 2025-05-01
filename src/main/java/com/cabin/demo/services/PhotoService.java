package com.cabin.demo.services;

import com.cabin.demo.dao.PhotoDao;
import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.dto.UserDto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.entity.photo.PhotoExif;
import com.cabin.demo.mapper.PhotoMapper;
import com.cabin.demo.util.ExifData;
import com.cabin.demo.util.ExifUtil;
import com.cabin.express.http.UploadedFile;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private final PhotoDao photoDao = new PhotoDao(HibernateUtil.getSessionFactory());

    private PhotoService() {
    }

    public static final PhotoService INSTANCE = new PhotoService();

    public long savePhoto(User user, UploadedFile file) throws Exception {
        Gson gson = new Gson();

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
            photo.setObjectKey("DSC09067.jpg");
            session.persist(photo);

            // Save EXIF Data
            PhotoExif photoExif = new PhotoExif();
            photoExif.setPhoto(photo); // Assuming a relationship is set
            photoExif.setExifJson(gson.toJsonTree(exifData.getExifMap()).toString());
            photoExif.setCameraModel(exifData.getCameraModel());
            photoExif.setLensModel(exifData.getLensModel());
            photoExif.setFocalLength(exifData.getFocalLength());
            photoExif.setExposureTime(exifData.getExposureTime());
            photoExif.setFNumber(exifData.getFNumber());
            photoExif.setIso(exifData.getIso());
            photoExif.setFlash(exifData.getFlash());
            photoExif.setShootingAt(exifData.getShootingTime());

            // gps
            photoExif.setLatitude(exifData.getLatitude());
            photoExif.setLongitude(exifData.getLongitude());

            session.persist(photoExif);

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

    public PhotoDto getPhotoDto(long id) {
        PhotoDto photoDto = null;
        try {
            Photo photo = photoDao.findById(id);
            photoDto = PhotoMapper.INSTANCE.toDto(photo);
        } catch (Exception ex) {
            log.error("Error getting photo DTO: {}", ex.getMessage());
        }
        return photoDto;
    }

    public List<PhotoDto> getSlicePhotoByUserId(Long userId, int offset, int limit) {
        List<PhotoDto> photoDtos = null;
        try {
            List<Photo> photos = photoDao.getSlicePhotoByUserId(userId, offset, limit);
            photoDtos = photos.stream()
                    .map(PhotoMapper.INSTANCE::toDto)
                    .toList();
        } catch (Exception ex) {
            log.error("Error getting slice of photos by user ID: {}", ex.getMessage());
        }
        return photoDtos;
    }
}
