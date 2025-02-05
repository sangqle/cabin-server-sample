package com.cabin.demo.router;

import com.cabin.demo.handler.PostHandler;
import com.cabin.express.router.Router;

public class PostRouter {
    private static final Router router = new Router();
    private static final String PREFIX = "/api/v1";

    public static Router getRouter() {
        setupRoutes();
        return router;
    }

    public static void setupRoutes() {
        router.setPrefix(PREFIX);

        router.get("/posts", PostHandler::getPosts);

        router.post("/posts", (req, res) -> {
            res.send();
        });

        router.put("/posts/:id", (req, res) -> {
            res.send();
        });

        router.delete("/posts/:id", (req, res) -> {
            res.send();
        });

    }
}
