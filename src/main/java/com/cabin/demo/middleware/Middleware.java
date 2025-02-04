package com.cabin.demo.middleware;

import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.middleware.MiddlewareChain;

public class Middleware {
    public static void logRequest(Request req, Response res, MiddlewareChain next) {
        try {
            System.out.println("Request received: " + req.getMethod() + " " + req.getPath());
            next.next(req, res);
        } catch (Exception ex) {
            System.out.println("Error in logging request: " + ex.getMessage());
        }
    }
}
