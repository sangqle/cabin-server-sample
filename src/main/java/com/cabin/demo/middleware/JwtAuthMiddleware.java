package com.cabin.demo.middleware;

import com.cabin.demo.dto.AuthenticatedUser;
import com.cabin.demo.util.JwtUtil;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.middleware.MiddlewareChain;

import java.io.IOException;

public class JwtAuthMiddleware {
    public static void authenticate(Request req, Response res, MiddlewareChain chain) throws IOException {
        String token = extractToken(req);
        if (token == null) {
            respondUnauthorized(res);
            return;
        }

        try {
            AuthenticatedUser authenticatedUser = JwtUtil.parseAndValidateToken(token);
            if (authenticatedUser == null) {
                respondUnauthorized(res);
                return;
            }
            req.putAttribute(AuthenticatedUser.class, authenticatedUser);
            chain.next(req, res);
        } catch (Exception e) {
            respondUnauthorized(res);
        }
    }

    private static String extractToken(Request req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private static void respondUnauthorized(Response res) throws IOException {
        res.setStatusCode(401);
        res.send("Unauthorized");
    }
}