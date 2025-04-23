package com.cabin.demo.server;

import com.cabin.demo.middleware.Middleware;
import com.cabin.demo.router.PostRouter;
import com.cabin.demo.router.UploadRouter;
import com.cabin.demo.router.UserRouter;
import com.cabin.express.server.CabinServer;
import com.cabin.express.server.ServerBuilder;

public class HServer {
    public static void setupAndStartServer() {
        try {
            CabinServer server = new ServerBuilder().setMaxPoolSize(200).setDefaultPoolSize(10).build();

            // Setup routes
            server.use(UploadRouter.getRouter());

            server.use(Middleware::logRequest);

            server.start();
        } catch (Exception ex) {
            System.out.println("Error starting server: " + ex.getMessage());
        }
    }
}
