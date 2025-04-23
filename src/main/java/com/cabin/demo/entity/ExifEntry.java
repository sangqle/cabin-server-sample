package com.cabin.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "photo_exif",
        indexes = @Index(name = "idx_exif_photo", columnList = "photo_id"))
public class ExifEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_space", nullable = false)
    private String tagSpace;                       // e.g. "ExifIFD"

    @Column(name = "tag_space_id", nullable = false)
    private int tagSpaceId;                        // usually 0

    @Column(nullable = false)
    private String tag;                            // e.g. "FNumber"

    @Column(nullable = false)
    private String label;                          // e.g. "Aperture"

    @Lob
    @Column(name = "raw_content", nullable = false,
            columnDefinition = "TEXT")
    private String rawContent;                     // e.g. "4.0"

    @Lob
    @Column(name = "clean_content",
            columnDefinition = "TEXT")
    private String cleanContent;                   // e.g. "f/4.0" (nullable)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    // Constructors, getters/setters omitted for brevity
}
