package com.cabin.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoDto {
    private String id;
    private String title;
    private String objectKey;
    private UserDto user;
    private PhotoExifDto exif;


    @Override
    public String toString() {
        return "PhotoDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", objectKey='" + objectKey + '\'' +
                ", user=" + user +
                ", exif=" + exif +
                '}';
    }
}