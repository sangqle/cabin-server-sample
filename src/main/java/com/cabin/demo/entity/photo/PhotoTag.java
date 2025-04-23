package com.cabin.demo.entity.photo;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "photo_tags")
public class PhotoTag {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public PhotoTag() {}

    public PhotoTag(Photo photo, Tag tag) {
        this.photo = photo;
        this.tag   = tag;
    }
}
