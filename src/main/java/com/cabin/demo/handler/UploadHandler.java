package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.demo.services.PhotoService;
import com.cabin.demo.services.UserService;
import com.cabin.express.config.Environment;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;


public class UploadHandler {
    private static final Logger _logger = LoggerFactory.getLogger(UploadHandler.class);

    private static final UserService userService = UserService.INSTANCE;

    private static void sendErrorResponse(Response resp, int statusCode, String message) throws IOException {
        resp.setStatusCode(statusCode);
        ApiResponse<String> response = ApiResponse.error(message);
        resp.writeBody(response);
    }

    private static void sendSuccessResponse(Response resp, String message) throws IOException {
        ApiResponse<String> response = ApiResponse.success(message);
        resp.writeBody(response);
    }

    public static void uploadPhoto(Request req, Response resp) {
        try {
            List<UploadedFile> files = req.getUploadedFile("files");

            if (files == null || files.isEmpty()) {
                sendErrorResponse(resp, 400, "No files uploaded");
                return;
            }

            User user = userService.getUserById(1L); // Example user ID
            for (UploadedFile file : files) {
                long photoId = PhotoService.INSTANCE.savePhoto(user, file);
                if (photoId < 0) {
                    _logger.error(String.format("Failed to save photo for user %s", user.getId()));
                }
            }

            sendSuccessResponse(resp, "Files uploaded successfully");
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }

    public static void getPresignedUrl(Request req, Response resp) {
        try {
            String objectKey = String.format("photos/test/%s", req.getQueryParam("objectKey"));
            String s3Bucket = PhotoService.INSTANCE.getR2PresignedUrl(Environment.getString("S3_BUCKET"), objectKey, Duration.ofMinutes(1));
            ApiResponse<String> response = ApiResponse.success(s3Bucket);
            resp.writeBody(response);
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}
