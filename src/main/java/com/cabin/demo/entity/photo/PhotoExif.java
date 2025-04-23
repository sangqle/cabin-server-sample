package com.cabin.demo.entity.photo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PhotoExif {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id")
    private Photo photo;

    private String rawData;
}
