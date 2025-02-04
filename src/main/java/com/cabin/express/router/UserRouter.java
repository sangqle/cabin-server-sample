package com.cabin.express.router;


import com.cabin.express.handler.UserHandler;

public class UserRouter {

    private static final Router router = new Router();
    private static final String PREFIX = "/api";

    private UserRouter() {
    }

    public static Router getRouter() {
        setupRoutes();
        return router;
    }

    public static void setupRoutes() {
        router.setPrefix(PREFIX);

        router.get("/users", UserHandler::getAllUsers);

        router.post("/users", UserHandler::addUser);

        router.put("/users/:id", (req, res) -> {
            res.send();
        });

        router.delete("/users/:id", (req, res) -> {
            res.send();
        });
    }

}
