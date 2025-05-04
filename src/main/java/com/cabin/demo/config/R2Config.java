package com.cabin.demo.config;

import java.time.Duration;

import com.cabin.express.config.Environment;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * Configuration for Cloudflare R2 (S3-compatible).
 */
@Data
@Builder
public class R2Config {
    /** Your R2 account ID, e.g. "d0abdafd067217c2e8af910ff38b5fbf" */
    private final String accountId;

    /** R2 Access Key ID */
    private final String accessKey;

    /** R2 Secret Access Key */
    private final String secretKey;

    private final String baseUrl;

    /** R2 bucket name */
    private final String bucketName;

    /** How long presigned URLs should live. */
    @Builder.Default
    private final Duration presignTtl = Duration.ofMinutes(15);

    /**
     * Loads R2Config from environment variables:
     *   R2_ACCOUNT_ID, R2_ACCESS_KEY, R2_SECRET_KEY,
     *   optional R2_PRESIGN_TTL_MINUTES.
     */
    public static R2Config fromEnv() {
        String accountId = Environment.getString("R2_ACCOUNT_ID");
        String accessKey = Environment.getString("R2_ACCESS_KEY");
        String secretKey = Environment.getString("R2_SECRET_KEY");
        String ttlMins   = Environment.getString("R2_PRESIGN_TTL_MINUTES");
        String baseUrl   = Environment.getString("R2_BASE_URL");
        String bucketName = Environment.getString("R2_BUCKET");

        Duration ttl = Duration.ofMinutes(15);
        if (ttlMins != null && !ttlMins.isBlank()) {
            ttl = Duration.ofMinutes(Long.parseLong(ttlMins.trim()));
        }

        return R2Config.builder()
                .accountId(accountId)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .presignTtl(ttl)
                .baseUrl(baseUrl)
                .bucketName(bucketName)
                .build();
    }

    /**
     * Ensures all required values are present.
     * Call this before using the config to fail fast on startup.
     */
    public void validate() {
        if (accountId   == null || accountId.isBlank()   ||
                accessKey   == null || accessKey.isBlank()   ||
                secretKey   == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing R2 configuration: " +
                            "R2_ACCOUNT_ID, R2_ACCESS_KEY, and R2_SECRET_KEY are required."
            );
        }
    }
}