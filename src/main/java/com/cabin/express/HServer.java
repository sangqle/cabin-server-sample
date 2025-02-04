package com.cabin.express;

import com.cabin.express.server.CabinServer;
import com.cabin.express.server.ServerBuilder;

public class HServer {
    public static void setupAndStartServer() {
        try {
            CabinServer server = new ServerBuilder().setMaxPoolSize(200).setDefaultPoolSize(10).build();
            server.enableMetricsLogging(true);
            server.start();
        } catch (Exception ex) {
            System.out.println("Error starting server: " + ex.getMessage());
        }
    }
}
