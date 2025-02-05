package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;


public class PostHandler {
    public static void getPosts(Request req, Response resp) {
        try {
            ApiResponse<String> response = ApiResponse.success("get posts");
            resp.writeBody(response);
            resp.send();
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}
