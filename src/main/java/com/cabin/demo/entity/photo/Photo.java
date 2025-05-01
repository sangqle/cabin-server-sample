package com.cabin.demo.entity.photo;

import com.cabin.demo.entity.album.AlbumPhoto;
import com.cabin.demo.entity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@Entity
@Table(
        name = "photos",
        indexes = {
                @Index(name = "idx_photos_user", columnList = "user_id"),
        }
)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "object_key",nullable = false, columnDefinition = "TEXT")
    private String objectKey;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "uploaded_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "photo", cascade = ALL, orphanRemoval = true)
    private PhotoExif photoExif;

//    @OneToMany(mappedBy = "photo",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true,
//            fetch = FetchType.LAZY)
//    private List<PhotoTag> photoTags = new ArrayList<>();
//
//    @OneToMany(mappedBy = "photo",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true,
//            fetch = FetchType.LAZY)
//    private List<AlbumPhoto> albumPhotos = new ArrayList<>();

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", objectKey='" + objectKey + '\'' +
                ", createdAt=" + createdAt +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
