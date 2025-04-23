package com.cabin.demo.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "photo_tags")
public class PhotoTag {

    @EmbeddedId
    private PhotoTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // Constructors, getters, setters omitted
}

