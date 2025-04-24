package com.cabin.demo.entity.photo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExifEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name") // This is the name of the tag
    private String tagName;

    @Column(name = "description") //
    private String description;

    @Column(name = "tag_type")
    private int tagType;

    @Column(name = "tag_type_hex")
    private String tagTypeHex;

    @Column(name = "directory_name")
    private String directoryName;
}
