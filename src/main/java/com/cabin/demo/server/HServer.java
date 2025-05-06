package com.cabin.demo.server;

import com.cabin.demo.middleware.JwtAuthMiddleware;
import com.cabin.demo.middleware.Middleware;
import com.cabin.demo.router.PhotoRouter;
import com.cabin.demo.router.UploadRouter;
import com.cabin.demo.router.UserRouter;
import com.cabin.express.config.Environment;
import com.cabin.express.router.Router;
import com.cabin.express.server.CabinServer;
import com.cabin.express.server.ServerBuilder;

public class HServer {
    public static void setupAndStartServer() {
        try {
            CabinServer server = new ServerBuilder()
                    .setMaxPoolSize(200)
                    .setDefaultPoolSize(10)
                    .setPort(Environment.getInteger("SERVER_PORT", 8888))
                    .build();

            // Setup routes
            Router userRouter = UserRouter.getRouter();
            Router photoRouter = PhotoRouter.getRouter();
            Router uploadRouter = UploadRouter.getRouter();

            photoRouter.use(JwtAuthMiddleware::authenticate);
            uploadRouter.use(JwtAuthMiddleware::authenticate);

            server.use(userRouter);
            server.use(photoRouter);
            server.use(uploadRouter);

            server.use(Middleware::logRequest);

            server.start();
        } catch (Exception ex) {
            System.out.println("Error starting server: " + ex.getMessage());
        }
    }
}
