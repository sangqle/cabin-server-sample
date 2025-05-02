package com.cabin.demo.entity.photo;

import com.cabin.demo.entity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Column(name = "raw_key", columnDefinition = "TEXT")
    private String rawKey;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "uploaded_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "photo", cascade = ALL, orphanRemoval = true)
    private PhotoExif photoExif;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "web_keys", columnDefinition = "jsonb")
    private Map<String, String> webKeys;


    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", raw_key" + rawKey + '\'' +
                ", createdAt=" + createdAt +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
