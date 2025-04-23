package com.cabin.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AlbumPhotoId implements Serializable {
    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "photo_id")
    private Long photoId;

    public AlbumPhotoId() {}

    public AlbumPhotoId(Long albumId, Long photoId) {
        this.albumId = albumId;
        this.photoId  = photoId;
    }

    // equals() and hashCode() based on both fields
}
