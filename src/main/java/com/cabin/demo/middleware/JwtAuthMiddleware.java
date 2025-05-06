package com.cabin.demo.middleware;

import com.cabin.demo.dto.AuthenticatedUser;
import com.cabin.demo.util.JwtUtil;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.middleware.MiddlewareChain;

import java.io.IOException;

public class JwtAuthMiddleware {
    public static void authenticate(Request req, Response res, MiddlewareChain chain) throws IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // Validate the token here (e.g., using a JWT library)
                AuthenticatedUser authenticatedUser = JwtUtil.parseAndValidateToken(token);
                // If valid, proceed to the next middleware or route handler
                if(authenticatedUser != null) {
                    req.putAttribute(AuthenticatedUser.class, authenticatedUser);
                } else {
                    // Token is invalid
                    res.setStatusCode(401);
                    res.send("Unauthorized");
                    return;
                }
                chain.next(req, res);
            } catch (Exception e) {
                // Token validation failed
                res.setStatusCode(401);
                res.send("Unauthorized");
            }
        } else {
            // No token provided
            res.setStatusCode(401);
            res.send("Unauthorized");
        }
    }
}
