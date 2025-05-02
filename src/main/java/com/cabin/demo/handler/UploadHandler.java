package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.demo.helper.R2Helper;
import com.cabin.demo.services.PhotoService;
import com.cabin.demo.services.UserService;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.http.UploadedFile;

import java.util.List;


public class UploadHandler {

    private static final UserService userService = UserService.INSTANCE;

    public static void uploadPhoto(Request req, Response resp) {
        try {
            ApiResponse<String> response;
            List<UploadedFile> files = req.getUploadedFile("files");

            if (files == null || files.isEmpty()) {
                resp.setStatusCode(400);
                response = ApiResponse.error("No files uploaded");
                resp.writeBody(response);
                return;
            }
            User user = userService.getUserById(1L); // Example user ID
            R2Helper r2Helper = R2Helper.getInstance();
            for (UploadedFile file : files) {
                PhotoService.INSTANCE.savePhoto(user, file);
            }
            response = ApiResponse.success("Files uploaded successfully");
            resp.writeBody(response);
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}
