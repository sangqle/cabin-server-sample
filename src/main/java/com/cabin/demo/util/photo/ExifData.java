package com.cabin.demo.util.photo;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ExifData {
    private String cameraModel;
    private String lensModel;
    private Integer iso;
    private String exposureTime;
    private BigDecimal fNumber;
    private BigDecimal focalLength;
    private String flash;
    private Double latitude;
    private Double longitude;
    private LocalDateTime shootingTime;

    private JsonElement exifJsonTree;

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