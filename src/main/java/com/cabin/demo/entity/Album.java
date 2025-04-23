package com.cabin.demo.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "album",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<AlbumPhoto> albumPhotos = new HashSet<>();

    // Constructors, getters, setters omitted
}
