package com.rest.demoapi.service;

import com.rest.demoapi.controller.ImageController;
import com.rest.demoapi.mapper.ImageMapper;
import com.rest.demoapi.model.ImageDto;
import com.rest.demoapi.repository.ImageRepository;
import com.rest.demoapi.repository.entity.ImageEntity;
import com.rest.demoapi.util.FileUtil;
import com.rest.demoapi.util.S3Util;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
@Profile("demo")
public class RdsService implements IImageService {

    private static final String IMAGE_UPLOAD_ACTION = "Image was uploaded";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private S3Util s3Util;
    @Autowired
    private INotificationService notificationService;

    @Transactional
    @Override
    public Optional<ImageDto> save(MultipartFile multipartFile) {
        String customName = RandomStringUtils.randomAlphanumeric(8);
        InputStream tempInput = FileUtil.getInputStream(multipartFile);
        s3Util.uploadObject(tempInput, multipartFile.getOriginalFilename(), customName);
        ImageEntity entity = new ImageEntity();
        entity.setName(customName);
        entity.setSize(multipartFile.getSize());
        entity.setFileExtension(FileUtil.getFileExtension(multipartFile.getOriginalFilename()));
        entity.setUpdatedAt(LocalDateTime.now());
        ImageEntity save = imageRepository.save(entity);
        ImageDto imageDto = imageMapper.toClientModel(save);
        notificationService.sendMessageToQueue(createMessage(imageDto));
        return Optional.of(imageDto);
    }

    @Transactional
    @Override
    public Optional<Resource> download(String name) {
        return Optional.ofNullable(s3Util.downloadObject(name));
    }

    @Transactional
    @Override
    public Optional<ImageDto> find(String id) {
        Optional<ImageEntity> entity = imageRepository.findById(Long.parseLong(id));
        return entity.isPresent() ? entity.map(e -> imageMapper.toClientModel(entity.get()))
                : Optional.empty();
    }

    @Transactional
    @Override
    public void delete(String id) {
        Optional<ImageEntity> entity = imageRepository.findById(Long.parseLong(id));
        if (entity.isPresent()) {
            entity.stream()
                    .map(ImageEntity::getName)
                    .forEach(s3Util::deleteObject);
            imageRepository.delete(entity.get());
        }
    }

    @Transactional
    @Override
    public List<ImageDto> findAll() {
        List<ImageEntity> entities = imageRepository.findAll();
        return entities.stream()
                .map(entityModel -> imageMapper.toClientModel(entityModel))
                .collect(Collectors.toList());
    }

    private String createMessage(ImageDto imageDto) {
        var downloadLink = linkTo(methodOn(ImageController.class).download(imageDto.getName()));
        return StringUtils.joinWith(":::", IMAGE_UPLOAD_ACTION, imageDto.toString(),
                downloadLink);
    }

}
