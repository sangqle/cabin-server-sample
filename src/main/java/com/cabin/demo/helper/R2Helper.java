package com.cabin.demo.helper;

import com.cabin.express.config.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;

public class R2Helper {
    private final S3Client s3;

    private static R2Helper instance;

    private R2Helper(String accountId, String accessKey, String secretKey) {
        // ★ Credentials
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider provider = StaticCredentialsProvider.create(creds);

        // ★ R2 endpoint & path-style
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        S3Configuration svcConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        this.s3 = S3Client.builder().endpointOverride(URI.create(endpoint)).credentialsProvider(provider).region(Region.of("auto")).serviceConfiguration(svcConfig).build();
    }

    public static R2Helper getInstance() {
        if (instance == null) {
            String S3_ACCESS_KEY = Environment.getString("S3_ACCESS_KEY", "");
            String S3_SECRET_KEY = Environment.getString("S3_SECRET_KEY", "");
            String S3_ACCOUNT_ID = Environment.getString("S3_ACCOUNT_ID", "");
            if (S3_ACCESS_KEY.isEmpty() || S3_SECRET_KEY.isEmpty() || S3_ACCOUNT_ID.isEmpty()) {
                throw new RuntimeException("R2 credentials are not set in the environment variables.");
            }
            instance = new R2Helper(S3_ACCOUNT_ID, S3_ACCESS_KEY, S3_SECRET_KEY);
        }
        return instance;
    }

    /**
     * Uploads the given byte array to the specified R2 bucket under the given object key.
     *
     * @param bucketName the name of the R2 bucket
     * @param objectKey  the key (path/filename) to store the object under
     * @param data       the binary content to upload
     */
    public void uploadObject(String bucketName, String objectKey, byte[] data) {
        try {
            PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();
            s3.putObject(request, RequestBody.fromBytes(data));
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload object to R2: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Uploads the given file to the specified R2 bucket under the given object key.
     */
    public String uploadPhoto(String bucketName, String objectKey, byte[] data) {
        try {
            // Allow all image content types
            String[] allowedContentTypes = {"image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"};
            String contentType = "image/jpeg"; // Default content type
            for (String type : allowedContentTypes) {
                if (objectKey.endsWith(type)) {
                    contentType = type;
                    break;
                }
            }
            // Set the content type in the request
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();
            // Upload the object
            s3.putObject(request, RequestBody.fromBytes(data));
            return String.format("https://%s.r2.cloudflarestorage.com/%s/%s", Environment.getString("S3_ACCOUNT_ID"), bucketName, objectKey);
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload object to R2: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    public void deleteObject(String bucketName, String objectKey) {
        try {
            s3.deleteObject(b -> b.bucket(bucketName).key(objectKey));
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to delete object from R2: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Returns the Presigned URL for the given object key in the specified bucket.
     */
    public String getPresignedUrl(String bucketName, String objectKey) {
        try {
            return s3.utilities().getUrl(b -> b.bucket(bucketName).key(objectKey)).toString();
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to get presigned URL: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}
