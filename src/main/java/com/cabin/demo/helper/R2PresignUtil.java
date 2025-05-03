package com.cabin.demo.helper;

import java.net.URI;
import java.time.Duration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

public class R2PresignUtil {
    private final S3Presigner presigner;

    /**
     * @param accountId Your R2 account ID (for endpoint URL)
     * @param accessKey R2 Access Key ID
     * @param secretKey R2 Secret Access Key
     */
    public R2PresignUtil(String accountId, String accessKey, String secretKey) {
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        this.presigner = S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))                        // R2 uses account-specific endpoint :contentReference[oaicite:0]{index=0}
                .credentialsProvider(StaticCredentialsProvider.create(creds))  // static creds :contentReference[oaicite:1]{index=1}
                .region(Region.of("auto"))                                     // R2 ignores AWS regions, “auto” works :contentReference[oaicite:2]{index=2}
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)                         // path-style addressing required by R2 :contentReference[oaicite:3]{index=3}
                                .build()
                )
                .build();
    }

    /**
     * Generates a pre-signed PUT URL for the given bucket/key.
     * @param bucketName The R2 bucket name
     * @param objectKey  The object key (e.g. "photos/2025/05/img.dng")
     * @param expiration How long the URL should remain valid
     * @return A fully-signed URL that clients can PUT to directly
     */
    public String getPresignedUrl(String bucketName, String objectKey, Duration expiration) {
        // 1) Build the underlying PutObjectRequest :contentReference[oaicite:4]{index=4}
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        // 2) Wrap in a presign request with TTL :contentReference[oaicite:5]{index=5}
        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .putObjectRequest(putReq)
                .signatureDuration(expiration)
                .build();

        // 3) Generate the presigned URL :contentReference[oaicite:6]{index=6}
        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignReq);

        return presigned.url().toString();
    }

    /**
     * Close the presigner when shutting down your app.
     */
    public void close() {
        presigner.close();
    }
}
