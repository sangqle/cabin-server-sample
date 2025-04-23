package com.cabin.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.List;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    private String id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="exif_data", columnDefinition="JSONB")
    private List<Map<String,Object>> exif;  // stores the raw EXIF array
}
