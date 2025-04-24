package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.demo.helper.R2Helper;
import com.cabin.demo.services.PhotoService;
import com.cabin.demo.util.ExifData;
import com.cabin.demo.util.ExifUtil;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.http.UploadedFile;

import java.util.List;
import java.util.Map;

public class UploadHandler {

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
            R2Helper r2Helper = R2Helper.getInstance();
            for (UploadedFile file : files) {
                // Process each file as needed
                long photoId = PhotoService.INSTANCE.savePhoto(file);
                System.err.println("Photo ID: " + photoId);
//                r2Helper.uploadPhoto("openext-photo", file.getFileName(), content);
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
