package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.express.http.Response;

import java.io.IOException;

public class BaseHandler {
    public static void sendSuccessResponse(Response resp, Object data) throws IOException {
        ApiResponse<Object> success = ApiResponse.success(data);
        resp.writeBody(success);
        resp.send();
    }

    public static void sendErrorResponse(Response resp, int statusCode, String message) throws IOException {
        ApiResponse<Object> error = ApiResponse.error(message);
        resp.setStatusCode(statusCode);
        resp.writeBody(error);
        resp.send();
    }
}
