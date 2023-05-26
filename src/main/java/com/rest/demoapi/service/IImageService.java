package com.rest.demoapi.service;

import com.rest.demoapi.model.ImageDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IImageService {

    Optional<ImageDto> save(MultipartFile multipartFile);

    Optional<Resource> download(String name);

    Optional<ImageDto> find(String id);

    void delete(String id);

    List<ImageDto> findAll();

}
