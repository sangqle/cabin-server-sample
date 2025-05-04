package com.cabin.demo.helper;

import com.cabin.demo.config.R2Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;

public class R2Helper {
    private final S3Client s3;

    public R2Helper(R2Config r2Config) {

        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", r2Config.getAccountId());

        AwsBasicCredentials creds = AwsBasicCredentials.create(r2Config.getAccessKey(), r2Config.getSecretKey());

        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .region(Region.of("auto"))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
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
    public int uploadPhoto(String bucketName, String objectKey, byte[] data) {
        try {
            // 1. Determine content type by file extension
            String contentType = "application/octet-stream";
            String lowerKey = objectKey.toLowerCase();
            if (lowerKey.endsWith(".jpg") || lowerKey.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerKey.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerKey.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerKey.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerKey.endsWith(".svg")) {
                contentType = "image/svg+xml";
            }

            // 2. Build the PutObjectRequest
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            // 3. Upload the object
            PutObjectResponse putResp = s3.putObject(
                    request,
                    RequestBody.fromBytes(data)
            );

            // 4. Check HTTP status via sdkHttpResponse().isSuccessful()
            if (putResp.sdkHttpResponse().isSuccessful()) {
                System.err.println("Upload succeeded, ETag: " + putResp.eTag());
                return 1;   // positive value indicates success
            } else {
                System.err.println("Upload failed, HTTP status: " +
                        putResp.sdkHttpResponse().statusCode());
                return 0;
            }
        } catch (S3Exception e) {
            // 5. Handle SDK-level errors (network, auth, etc.)
            System.err.println("Failed to upload object to R2: " +
                    e.awsErrorDetails().errorMessage());
            return 0;
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
