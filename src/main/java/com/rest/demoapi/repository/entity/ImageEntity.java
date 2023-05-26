package com.rest.demoapi.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Data
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image_size")
    private long size;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
