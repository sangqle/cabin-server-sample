package com.cabin.demo.helper;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioHelper {
    private final MinioClient client;
    private final String endpoint;

    /**
     * @param endpoint  Your MinIO S3-compatible endpoint, e.g. "https://minio.local:9000"
     * @param accessKey MinIO access key
     * @param secretKey MinIO secret key
     */
    public MinioHelper(String endpoint, String accessKey, String secretKey) {
        this.endpoint = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        this.client = MinioClient.builder().endpoint(this.endpoint).credentials(accessKey, secretKey).build();
    }

    /**
     * Uploads the given byte[] to MinIO under bucketName/objectKey
     * and returns the full URL of the uploaded object.
     *
     * @param bucketName The target bucket
     * @param objectKey  The target key (path + filename)
     * @param data       The raw bytes to upload
     * @return The public URL (endpoint/bucketName/objectKey)
     * @throws IOException    On I/O errors
     * @throws MinioException On MinIO SDK errors
     */
    public String uploadPhoto(String bucketName, String objectKey, byte[] data)
            throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        boolean bucketExists = client.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        if (!bucketExists) {
            client.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        }

        client.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .stream(new ByteArrayInputStream(data), data.length, -1)
                        .contentType("image/jpeg")
                        .build()
        );

        return String.format("%s/%s/%s", endpoint, bucketName, objectKey);
    }
}
