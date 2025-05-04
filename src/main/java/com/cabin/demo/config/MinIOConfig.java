package com.cabin.demo.config;

import java.time.Duration;

import com.cabin.express.config.Environment;
import lombok.Builder;
import lombok.Data;

/**
 * Configuration for MinIO (S3-compatible).
 */
@Data
@Builder
public class MinIOConfig {
    /** MinIO endpoint, e.g. "https://minio.local:9000" */
    private final String endpoint;

    /** MinIO Access Key */
    private final String accessKey;

    /** MinIO Secret Access Key */
    private final String secretKey;

    /** How long presigned URLs should live. */
    @Builder.Default
    private final Duration presignTtl = Duration.ofMinutes(15);

    /**
     * Loads MinIoConfig from environment variables:
     *   MINIO_ENDPOINT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY,
     *   optional MINIO_PRESIGN_TTL_MINUTES.
     */
    public static MinIOConfig fromEnv() {
        String endpoint = Environment.getString("MINIO_ENDPOINT");
        String accessKey = Environment.getString("MINIO_ACCESS_KEY");
        String secretKey = Environment.getString("MINIO_SECRET_KEY");
        String ttlMins = Environment.getString("MINIO_PRESIGN_TTL_MINUTES");

        Duration ttl = Duration.ofMinutes(15);
        if (ttlMins != null && !ttlMins.isBlank()) {
            ttl = Duration.ofMinutes(Long.parseLong(ttlMins.trim()));
        }

        return MinIOConfig.builder()
                .endpoint(endpoint)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .presignTtl(ttl)
                .build();
    }
}