package com.cabin.express.server;

import com.cabin.express.middleware.Middleware;
import com.cabin.express.router.AppRouter;

public class HServer {
    public static void setupAndStartServer() {
        try {
            CabinServer server = new ServerBuilder().setMaxPoolSize(200).setDefaultPoolSize(10).build();

            // Setup routes
            server.use(AppRouter.getRouter());
            server.use(Middleware::logRequest);

//            server.enableMetricsLogging(true);
            server.start();
        } catch (Exception ex) {
            System.out.println("Error starting server: " + ex.getMessage());
        }
    }
}
