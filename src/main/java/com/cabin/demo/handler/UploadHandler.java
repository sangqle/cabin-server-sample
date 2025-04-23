package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.demo.helper.R2Helper;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.http.UploadedFile;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public class UploadHandler {

    public static void getMetaDataFromFile(byte[] content) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            // Extract metadata from the image content
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            // Iterate through all directories and tags
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    String description = tag.getDescription();
                    int tagType = tag.getTagType();
                    String tagTypeHex = tag.getTagTypeHex();
                    String directoryName = tag.getDirectoryName();

                    System.err.println(String.format("Tag: %s, Description: %s, Type: %d, Type Hex: %s, Directory: %s",
                            tagName, description, tagType, tagTypeHex, directoryName));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                System.out.println("Uploaded file: " + file.getFileName());
                byte[] content = file.getContent();
                Map<String, String> allMetadata = file.getAllMetadata();
                System.err.println("File Metadata: " + allMetadata);
                getMetaDataFromFile(content);

                // You can save the file or perform other operations here
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
