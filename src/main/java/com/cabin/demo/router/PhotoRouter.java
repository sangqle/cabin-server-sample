package com.cabin.demo.router;

import com.cabin.demo.handler.PhotoHandler;
import com.cabin.express.router.Router;

public class PhotoRouter {
    private static final Router router = new Router();
    private static final String PREFIX = "/api/v1/photos";

    public static Router getRouter() {
        setupRoutes();
        return router;
    }

    public static void setupRoutes() {
        router.setPrefix(PREFIX);
        router.get("/", PhotoHandler::getPosts);
    }
}
