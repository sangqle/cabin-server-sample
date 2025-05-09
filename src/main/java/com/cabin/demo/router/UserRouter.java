package com.cabin.demo.router;

import com.cabin.demo.handler.UserHandler;
import com.cabin.express.router.Router;

public class UserRouter {

    private static final Router router = new Router();
    private static final String PREFIX = "/api/v1";

    private UserRouter() {
    }

    public static Router getRouter() {
        setupRoutes();
        return router;
    }

    public static void setupRoutes() {
        router.setPrefix(PREFIX);

        router.get("/users", UserHandler::getAllUsers);

        router.post("/users", UserHandler::register);

        router.post("/users/login", UserHandler::login);

        router.put("/users/:id", (req, res) -> {
            res.send();
        });

        router.delete("/users/:id", (req, res) -> {
            res.send();
        });
    }
}
