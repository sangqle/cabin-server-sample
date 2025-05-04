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
import com.cabin.demo.util.ExifData;
import com.cabin.demo.util.ExifUtil;
import com.cabin.demo.util.HttpUtil;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.demo.util.photo.FileNameEncoder;
import com.cabin.express.config.Environment;
import com.cabin.express.http.UploadedFile;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private final PhotoDao photoDao = new PhotoDao(HibernateUtil.getSessionFactory());
    private static final String R2_BUCKET = Environment.getString("R2_BUCKET");

    MinioHelper minioHelper = ServiceLocator.get(MinioHelper.class);
    R2Helper r2Helper = ServiceLocator.get(R2Helper.class);

    R2PresignUtil r2PresignUtil = new R2PresignUtil(
            Environment.getString("R2_ACCOUNT_ID", ""),
            Environment.getString("R2_ACCESS_KEY", ""),
            Environment.getString("R2_SECRET_KEY", "")
    );

    HttpUtil http = new HttpUtil("http://localhost:8000");


    private PhotoService() {
    }

    public static final PhotoService INSTANCE = new PhotoService();

    public long savePhoto(User user, UploadedFile file) throws Exception {
        Gson gson = new Gson();

        long savedPhotoId = -1;
        String rawKey = null;
        List<String> uploadedKeys = new ArrayList<>();

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
            session.persist(photo);

            // Force INSERT now so photo.id is generated
            session.flush();
            savedPhotoId = photo.getId();

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

            // 2. Build R2 object keys
            String userEncId = IdObfuscator.encodeUserId(user.getId());
            String photoEncId = IdObfuscator.encodePhotoId(savedPhotoId);

            // Raw object key
            rawKey = String.format(
                    "photos/%s/%s/raw/%s",
                    userEncId,
                    photoEncId,
                    FileNameEncoder.encode(file.getFileName())
            );

            // 3. Upload raw file
            int rs = r2Helper.uploadPhoto(R2_BUCKET, rawKey, content);
            if (rs < 0) {
                throw new Exception("Failed to upload raw file");
            }

            // 5. Update entity with keys and commit
            photo.setRawKey(rawKey);
            session.merge(photo);

            transaction.commit();
            photoId = photo.getId();
        } catch (Exception ex) {
            log.error("Error saving photo: {}", ex.getMessage());
            // 6. Compensation: delete any uploaded objects to avoid orphans
            if (rawKey != null) {
                r2Helper.deleteObject(R2_BUCKET, rawKey);
            }
            return -1;
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
