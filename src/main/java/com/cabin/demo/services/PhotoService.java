package com.cabin.demo.services;

import com.cabin.demo.dao.PhotoDao;
import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.entity.photo.PhotoExif;
import com.cabin.demo.helper.R2Helper;
import com.cabin.demo.helper.R2PresignUtil;
import com.cabin.demo.mapper.PhotoMapper;
import com.cabin.demo.util.ExifData;
import com.cabin.demo.util.ExifUtil;
import com.cabin.demo.util.HttpUtil;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.demo.util.photo.FileNameEncoder;
import com.cabin.demo.util.photo.ImageUtils;
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
    private static final String S3_BUCKET = Environment.getString("S3_BUCKET");
    R2Helper r2Helper = R2Helper.getInstance();

    R2PresignUtil r2PresignUtil = new R2PresignUtil(
            Environment.getString("S3_ACCOUNT_ID"),
            Environment.getString("S3_ACCESS_KEY"),
            Environment.getString("S3_SECRET_KEY")
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
            r2Helper.uploadPhoto(S3_BUCKET, rawKey, content);
//            http.uploadRaw(content, rawKey, file.getFileName(), file.getContentType());

            // 4. Generate & upload web-optimized versions
            Map<String, String> webKeys = new HashMap<>();
            for (String size : List.of("original", "medium")) {
                String webKey = String.format(
                        "photos/%s/%s/web/%s.jpg",
                        userEncId,
                        photoEncId,
                        size
                );
//                r2Helper.uploadPhoto(S3_BUCKET, webKey, resized);
                String s = http.uploadAndConvert(content, webKey, size);
                System.err.println("rs: " + s);
                uploadedKeys.add(webKey);
                webKeys.put(size, webKey);
            }

            // 5. Update entity with keys and commit
            photo.setRawKey(rawKey);
            photo.setWebKeys(webKeys);
            session.merge(photo);

            transaction.commit();
            photoId = photo.getId();
        } catch (Exception ex) {
            log.error("Error saving photo: {}", ex.getMessage());

//            // 6. Compensation: delete any uploaded objects to avoid orphans
//            if (rawKey != null) {
//                r2Helper.deleteObject("openext-photo", rawKey);
//            }
//            for (String key : uploadedKeys) {
//                r2Helper.deleteObject("openext-photo", key);
//            }
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
