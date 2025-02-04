package com.cabin.express.router;


import com.cabin.express.handler.AppHandler;

public class AppRouter {

    private static final Router router = new Router();
    private static final String PREFIX = "/api";

    private AppRouter() {
    }

    public static Router getRouter() {
        setupRoutes();
        return router;
    }

    public static void setupRoutes() {
        router.setPrefix(PREFIX);

        router.get("/users", AppHandler::getUsers);

        router.post("/users", (req, res) -> {
            res.send();
        });

        router.put("/users/:id", (req, res) -> {
            res.send();
        });

        router.delete("/users/:id", (req, res) -> {
            res.send();
        });
    }

}
