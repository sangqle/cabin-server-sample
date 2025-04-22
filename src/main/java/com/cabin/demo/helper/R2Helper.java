package com.cabin.demo.helper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

public class R2Helper {
    private final S3Client s3;

    public R2Helper(String accountId,
                    String accessKey,
                    String secretKey) {
        // ★ Credentials
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider provider = StaticCredentialsProvider.create(creds);

        // ★ R2 endpoint & path‑style
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        S3Configuration svcConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(provider)
                .region(Region.of("auto"))
                .serviceConfiguration(svcConfig)
                .build();
    }
}
