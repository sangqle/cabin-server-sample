package com.cabin.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PhotoTagId implements Serializable {
    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "tag_id")
    private Long tagId;

    // Default constructor required by JPA
    public PhotoTagId() {}

    public PhotoTagId(Long photoId, Long tagId) {
        this.photoId = photoId;
        this.tagId = tagId;
    }

    // equals() and hashCode() using both fields
}
