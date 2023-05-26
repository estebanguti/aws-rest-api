package com.rest.demoapi.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ImageDto {

    private String id;
    private String name;
    private long size;
    private String fileExtension;
    private LocalDateTime updatedAt;
    private Map<String, String> metadata;

}
