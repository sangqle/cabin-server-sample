package com.cabin.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class HttpUtil {
    private final HttpClient client;
    private final String baseUrl;
    private final Gson gson = new Gson();

    /**
     * @param baseUrl e.g. "https://your-python-api"
     */
    public HttpUtil(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Calls POST /upload_raw
     * @param fileBytes   raw bytes to upload
     * @param objectKey   the R2 key under which to store it
     * @param fileName    original filename (for Content-Disposition)
     * @param contentType MIME type of the file (e.g. "image/jpeg")
     * @return the JSON "url" field from the response
     */
    public String upload(byte[] fileBytes, String objectKey)
            throws IOException, InterruptedException {

        // build multipart entity
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", fileBytes,
                        ContentType.MULTIPART_FORM_DATA, objectKey)
                .addTextBody("object_key", objectKey, ContentType.TEXT_PLAIN)
                .build();

        // serialize to a byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        entity.writeTo(baos);
        byte[] body = baos.toByteArray();

        // build request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/upload"))
                .header("Content-Type", entity.getContentType().getValue())
                .POST(BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2) {
            throw new IOException("uploadRaw failed: " + resp.statusCode() + " → " + resp.body());
        }

        // parse { "url": "...", "key": "..." }
        JsonObject json = gson.fromJson(resp.body(), JsonObject.class);
        return json.get("url").getAsString();
    }

    public String uploadAndConvert(byte[] rawBytes, String objectKey, String compressType)
            throws IOException, InterruptedException {

        String uri = baseUrl + "/upload_convert?object_key="
                + URLEncoder.encode(objectKey, StandardCharsets.UTF_8)
                + "&compress_type=" + URLEncoder.encode(compressType, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(rawBytes))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2) {
            throw new IOException("uploadConvert failed: "
                    + resp.statusCode() + " → " + resp.body());
        }

        JsonObject json = gson.fromJson(resp.body(), JsonObject.class);
        return json.get("url").getAsString();
    }
}
