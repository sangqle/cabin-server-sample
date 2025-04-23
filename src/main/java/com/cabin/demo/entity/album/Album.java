package com.cabin.demo.entity.album;

import com.cabin.demo.entity.auth.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP(3) WITHOUT TIME ZONE")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "album",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<AlbumPhoto> albumPhotos = new ArrayList<>();

    public Album() {}

    public Album(User user, String name) {
        this.user = user;
        this.name = name;
    }

    // getters and setters...
}
