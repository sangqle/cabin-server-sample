package com.cabin.demo.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(
            mappedBy = "tag",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PhotoTag> photoTags = new HashSet<>();

    // Constructors, getters, setters omitted for brevity
}
