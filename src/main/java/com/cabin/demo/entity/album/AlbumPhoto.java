package com.cabin.demo.entity.album;

import com.cabin.demo.entity.photo.Photo;
import jakarta.persistence.*;

@Entity
@Table(name = "album_photos")
public class AlbumPhoto {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Column(name = "position")
    private Integer position;  // ordering within the album

    // Constructors, getters, setters omitted
}
