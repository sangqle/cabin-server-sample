package com.cabin.demo.exception;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.express.http.Response;
import java.util.NoSuchElementException;

public class GlobalExceptionHandler {
    public static void handleException(Exception e, Response resp) {
        try {
            int statusCode = 500;
            String message = "Internal Server Error";

            if (e instanceof IllegalArgumentException) {
                statusCode = 400;
                message = e.getMessage();
            } else if (e instanceof NoSuchElementException) {
                statusCode = 404;
                message = "Resource not found";
            } else {
                e.printStackTrace();
            }
            resp.setStatusCode(statusCode);
            ApiResponse<String> error = ApiResponse.error(message);
            resp.writeBody(error);
            resp.send();
        } catch (Throwable ex) {
            System.out.println("Error in handling exception: " + ex);
        }
    }
}