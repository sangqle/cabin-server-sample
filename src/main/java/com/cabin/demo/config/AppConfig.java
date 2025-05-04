package com.cabin.demo.config;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppConfig {
    MinIOConfig minioConfig;
    DBConfig dbConfig;

    public static AppConfig fromEnv() {
        return AppConfig.builder()
                .minioConfig(MinIOConfig.fromEnv())
                .dbConfig(DBConfig.fromEnv())
                .build();
    }
}
