package com.cabin.demo.entity.photo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "photo_exif")
public class PhotoExif {
    @Id
    @Column(name = "photo_id")
    private Long photoId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "photo_id")
    private Photo photo;

    // — promoted, searchable EXIF fields —
    @Column(name = "camera_model", length = 100)
    private String cameraModel;

    @Column(name = "lens_model", length = 100)
    private String lensModel;

    @Column(name = "iso")
    private Integer iso;

    @Column(name = "exposure_time", length = 50)
    private String exposureTime;

    @Column(name = "f_number", precision = 5, scale = 2)
    private BigDecimal fNumber;

    @Column(name = "focal_length", precision = 7, scale = 2)
    private BigDecimal focalLength;

    @Column(name = "flash", length = 100)
    private String flash;

    // Longitude and Latitude
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="exif_json", columnDefinition="jsonb")
    private String exifJson;
}
