package com.cabin.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class PhotoDto {
    private String id;
    private String title;
    private String rawImageUrl;

    // Map of variant name → URL, e.g. "thumb"→".../thumb.jpg"
    private Map<String, String> urls = new HashMap<>();

    private UserDto user;
    private PhotoExifDto exif;

    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "PhotoDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urls=" + urls +
                ", user=" + user +
                ", exif=" + exif +
                '}';
    }
}
