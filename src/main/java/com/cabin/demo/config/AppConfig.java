package com.cabin.demo.config;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppConfig {
    MinIOConfig minioConfig;
    DBConfig dbConfig;
    R2Config r2Config;

    public static AppConfig fromEnv() {
        return AppConfig.builder()
                .minioConfig(MinIOConfig.fromEnv())
                .dbConfig(DBConfig.fromEnv())
                .r2Config(R2Config.fromEnv())
                .build();
    }
}
