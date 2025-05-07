package com.cabin.demo.services;

import com.cabin.demo.dao.PhotoDao;
import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.entity.photo.PhotoExif;
import com.cabin.demo.helper.MinioHelper;
import com.cabin.demo.helper.R2Helper;
import com.cabin.demo.helper.R2PresignUtil;
import com.cabin.demo.locator.ServiceLocator;
import com.cabin.demo.mapper.PhotoMapper;
import com.cabin.demo.util.photo.ExifData;
import com.cabin.demo.util.photo.ExifUtil;
import com.cabin.demo.util.HttpUtil;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.demo.util.photo.FileNameEncoder;
import com.cabin.demo.worker.RpcClient;
import com.cabin.express.config.Environment;
import com.cabin.express.http.UploadedFile;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.io.IOException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private final PhotoDao photoDao = new PhotoDao(HibernateUtil.getSessionFactory());
    private static final String R2_BUCKET = Environment.getString("R2_BUCKET");
    private final RpcClient rpcClient = new RpcClient();
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();



    MinioHelper minioHelper = ServiceLocator.get(MinioHelper.class);
    R2Helper r2Helper = ServiceLocator.get(R2Helper.class);

    R2PresignUtil r2PresignUtil = new R2PresignUtil(
            Environment.getString("R2_ACCOUNT_ID", ""),
            Environment.getString("R2_ACCESS_KEY", ""),
            Environment.getString("R2_SECRET_KEY", "")
    );


    private PhotoService() throws Exception {
    }

    public static final PhotoService INSTANCE;

    static {
        try {
            INSTANCE = new PhotoService();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long savePhoto(User user, UploadedFile file) throws Exception {
        // 1) extract EXIF once
        ExifData exifData = ExifUtil.getExifData(file.getContent());

        // 2) build and persist the Photo + PhotoExif in one transaction
        Photo photo = createPhotoAggregate(user, file.getFileName(), file.getFileName(), exifData);

        // 3) upload the raw bytes to R2 and store the key
        String rawKey = uploadRawAndSetKey(photo, file.getContent());

        // 4) asynchronously request conversion & DB update
        enqueueConversion(photo.getId(), rawKey);

        return photo.getId();
    }


    private Photo createPhotoAggregate(User user, String title, String description, ExifData exif) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Photo photo = new Photo();
            photo.setUser(user);
            photo.setTitle(title);
            photo.setDescription(description);

            PhotoExif photoExif = new PhotoExif();
            photoExif.setPhoto(photo);
            photoExif.populateFrom(exif);
            // assume PhotoExif has a helper that sets all its fields from ExifData

            photo.setPhotoExif(photoExif);
            session.persist(photo);  // cascades both photo + exif
            tx.commit();

            return photo;
        }
    }


    private String uploadRawAndSetKey(Photo photo, byte[] content) throws Exception {
        // build a stable key
        String userEnc  = IdObfuscator.encodeUserId(photo.getUser().getId());
        String photoEnc = IdObfuscator.encodePhotoId(photo.getId());
        String rawKey   = String.format("photos/%s/%s/raw/%s",
                userEnc, photoEnc,
                FileNameEncoder.encode(photo.getTitle()));

        // upload
        int res = r2Helper.uploadPhoto(R2_BUCKET, rawKey, content);
        if (res < 0) {
            throw new IOException("Failed to upload raw image to R2");
        }

        // persist the rawKey in its own transaction
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            photo.setRawKey(rawKey);
            session.merge(photo);
            tx.commit();
        }

        return rawKey;
    }

    private void enqueueConversion(long photoId, String rawKey) throws java.io.IOException {
        rpcClient.callConvert(photoId, rawKey)
                .thenAccept(json -> syncPhotoVariants(photoId, json))
                .exceptionally(ex -> {
                    // log & decide on retry or DLQ
                    log.error("Conversion RPC failed for {}: {}", photoId, ex.getMessage());
                    return null;
                });
    }

    private void syncPhotoVariants(long photoId, String json) {
        // parse JSON into a Map<String,String> of variant keys
        Map<String,String> variants = parseVariantKeys(json);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Photo photo = session.get(Photo.class, photoId);
            photo.setWebKeys(variants);
            session.merge(photo);
            tx.commit();
        }
    }

    // helper to parse the converter’s JSON payload
    private Map<String,String> parseVariantKeys(String json) {
        List<String> keysExpected = List.of("webKey", "mediumKey", "smallKey", "thumbKey", "originalKey");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        Map<String, String> keys = new HashMap<>();
        for (String key : keysExpected) {
            if (jsonObject.has(key)) {
                keys.put(key, jsonObject.get(key).getAsString());
            }
        }
    }

    public void markWebKeyReady(long photoId, String webKey) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Photo photo = session.get(Photo.class, photoId);
            if (photo != null) {
                Map<String, String> webKeys = new HashMap<>();
                webKeys.put("web", webKey);
                photo.setWebKeys(webKeys);
                session.merge(photo);
            }
            transaction.commit();
        } catch (Exception ex) {
            log.error("Error marking web key ready: {}", ex.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
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

    public String getR2PresignedUrl(String bucket, String objectKey, Duration duration) {
        String presignedUrl = null;
        try {
            presignedUrl = r2PresignUtil.getPresignedUrl(bucket, objectKey, duration);
        } catch (Exception ex) {
            log.error("Error getting presigned URL: {}", ex.getMessage());
        }
        return presignedUrl;
    }
}
