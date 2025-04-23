package com.cabin.demo.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExifData {
    private String cameraModel;
    private String lensModel;
    private Integer iso;
    private String exposureTime;
    private Double fNumber;
    private Double focalLength;
    private String flash;
    private Double latitude;
    private Double longitude;
    private LocalDateTime shootingTime;

    private String exifEntry;

    @Override
    public String toString() {
        return "ExifData{" +
                "cameraModel='" + cameraModel + '\'' +
                ", lensModel='" + lensModel + '\'' +
                ", iso=" + iso +
                ", exposureTime='" + exposureTime + '\'' +
                ", fNumber=" + fNumber +
                ", focalLength=" + focalLength +
                ", flash='" + flash + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", shootingTime=" + shootingTime +
                '}';
    }
}