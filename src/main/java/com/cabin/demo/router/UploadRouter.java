package com.cabin.demo.router;

import com.cabin.demo.handler.UploadHandler;
import com.cabin.express.router.Router;

public class UploadRouter {

    private static final Router router = new Router();
    private static final String PREFIX = "/api/v1/upload";

    private UploadRouter() {
    }

    public static Router getRouter() {
        setupRoutes();
        return router;
    }

    public static void setupRoutes() {
        router.setPrefix(PREFIX);
        router.post("/image", UploadHandler::uploadPhoto);
        router.get("/presigned-url", UploadHandler::getPresignedUrl);
    }
}
