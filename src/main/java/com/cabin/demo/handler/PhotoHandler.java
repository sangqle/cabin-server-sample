package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.demo.services.PhotoService;
import com.cabin.demo.util.LocalDateTimeAdapter;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.List;

public class PhotoHandler {
    // Build a Gson instance that knows how to handle LocalDateTime
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    LocalDateTime.class,
                    new LocalDateTimeAdapter()
            )
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static void getPosts(Request req, Response resp) {
        try {
            int offset = Integer.parseInt(req.getQueryParam("offset"));
            int limit = Integer.parseInt(req.getQueryParam("limit"));
            String userId = req.getPathParam("userId");
            List<PhotoDto> photos =
                    PhotoService.INSTANCE.getSlicePhotoByUserId(IdObfuscator.decodeUserId(userId), offset, limit);
            ApiResponse<List<PhotoDto>> response =
                    ApiResponse.success(photos);

            String json = GSON.toJson(response);  // Convert to JSON with Gson :contentReference[oaicite:4]{index=4}

            resp.setHeader("Content-Type", "application/json");
            resp.writeBody(json);
            resp.send();
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        }
    }
}
