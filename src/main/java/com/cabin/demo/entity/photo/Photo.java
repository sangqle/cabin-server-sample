package com.cabin.demo.entity.photo;

import com.cabin.demo.entity.album.AlbumPhoto;
import com.cabin.demo.entity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "photos",
        indexes = {
                @Index(name = "idx_photos_user", columnList = "user_id"),
                @Index(name = "idx_photos_camera_model", columnList = "camera_model"),
                @Index(name = "idx_photos_lens_model", columnList = "lens_model")
        }
)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "shooting_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime shootingAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "uploaded_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // — promoted, searchable EXIF fields —
    @Column(name="camera_model", length=100)
    private String cameraModel;

    @Column(name="lens_model", length=100)
    private String lensModel;

    @Column(name="iso")
    private Integer iso;

    @Column(name="exposure_time", length=50)
    private String exposureTime;

    @Column(name = "f_number", precision = 5, scale = 2)
    private Double fNumber;

    @Column(name="focal_length", precision = 7, scale = 2)
    private Double focalLength;

    /** New field for Flash info, e.g. "Off, did not fire" */
    @Column(name = "flash", length = 100)
    private String flash;

    @OneToMany(mappedBy = "photo",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<PhotoTag> photoTags = new ArrayList<>();

    @OneToMany(mappedBy = "photo",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<AlbumPhoto> albumPhotos = new ArrayList<>();
}
