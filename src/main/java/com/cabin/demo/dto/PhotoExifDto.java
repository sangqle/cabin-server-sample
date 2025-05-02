package com.cabin.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PhotoExifDto {
    private LocalDateTime shootingAt;
    private String cameraModel;
    private String lensModel;

    @Override
    public String toString() {
        return "PhotoExifDto{" +
//                "shootingAt=" + shootingAt +
                ", cameraModel='" + cameraModel + '\'' +
                ", lensModel='" + lensModel + '\'' +
                '}';
    }
}