package com.cabin.demo;

import com.cabin.demo.config.DBConfig;
import com.cabin.demo.config.R2Config;
import com.cabin.demo.helper.MinioHelper;
import com.cabin.demo.helper.R2Helper;
import com.cabin.demo.locator.ServiceLocator;
import com.cabin.demo.server.HServer;
import com.cabin.demo.config.AppConfig;

public class MainApp {
    public static void main(String[] args) {
        // 1) Load config once
        AppConfig config = AppConfig.fromEnv();

        // 2) Instantiate shared helpers
        MinioHelper minioHelper = new MinioHelper(config.getMinioConfig());
        R2Helper r2Helper = new R2Helper(config.getR2Config());
        DBConfig dbConfig = config.getDbConfig();

        // 3) Register into your framework’s context / service locator
        ServiceLocator.register(R2Config.class, config.getR2Config());

        ServiceLocator.register(MinioHelper.class, minioHelper);
        ServiceLocator.register(R2Helper.class, r2Helper);

        ServiceLocator.register(DBConfig.class, dbConfig);

        HServer.setupAndStartServer();
    }
}