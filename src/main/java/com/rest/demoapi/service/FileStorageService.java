package com.rest.demoapi.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.rest.demoapi.model.ImageDto;
import com.rest.demoapi.util.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Profile("local")
public class FileStorageService implements IImageService {

    private static final String BASE_PATH = "src/main/resources/images/";

    @SneakyThrows
    @Override
    public Optional<ImageDto> save(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String fileCode = RandomStringUtils.randomAlphanumeric(8);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path destination = Paths.get(BASE_PATH + fileCode + "/" + fileName);

            if (!Files.exists(destination)) {
                Files.createDirectories(destination);
            }

            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);

            ImageDto imageDto = new ImageDto();
            imageDto.setId(fileCode);
            imageDto.setName(fileName);
            imageDto.setSize(multipartFile.getSize());
            imageDto.setFileExtension(FileUtil.getFileExtension(fileName));
            imageDto.setUpdatedAt(LocalDateTime.now());
            imageDto.setMetadata(getMetadata(fileCode));

            return Optional.of(imageDto);

        } catch (IOException e) {
            log.error("Could not save file {} due to this error: {}" + fileName, e);
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    public Optional<Resource> download(String id) {
        Path pathFile = getFolderImagePath(id);
        Optional<Path> file = Files.list(pathFile).findFirst();
        return file.isPresent() ? Optional.of(new UrlResource(file.get().toUri())) : Optional.empty();
    }

    @SneakyThrows
    @Override
    public Optional<ImageDto> find(String id) {
        Optional<ImageDto> optional = Optional.empty();
        Path pathFile = getFolderImagePath(id);
        try {
            Iterator<Path> files = Files.list(pathFile).iterator();
            while (files.hasNext()) {
                Path file = files.next();
                ImageDto image = new ImageDto();
                image.setId(id);
                image.setName(file.getFileName().toString());
                image.setMetadata(getMetadata(id));
                optional = Optional.of(image);
            }
        } catch (Exception e) {
            log.error("Could not found file {} due to this error: {}" + id, e);
        }
        return optional;
    }

    @Override
    public void delete(String id) {
        Path pathFile = getFolderImagePath(id);
        try {
            FileUtils.deleteDirectory(new File(pathFile.toUri()));
        } catch (IOException e) {
            log.error("Could not delete file {} due to this error: {}", pathFile, e);
        }
    }

    @Override
    public List<ImageDto> findAll() {
        List<ImageDto> images = new ArrayList<>(List.of());
        Path path = Paths.get(BASE_PATH);
        List<File> files = List.of(new File(path.toUri()).listFiles(File::isDirectory));
        for (File file : files) {
            File fileImg = Arrays.stream(file.listFiles()).findFirst().get();
            ImageDto image = new ImageDto();
            image.setId(file.getName());
            image.setName(fileImg.getName());
            image.setMetadata(getMetadata(file.getName()));
            images.add(image);
        }
        return images;
    }

    @SneakyThrows
    private Path getFolderImagePath(String path) {
        return Paths.get(BASE_PATH + path);
    }

    @SneakyThrows
    private Map<String, String> getMetadata(String id) {
        Path pathFile = getFolderImagePath(id);
        Optional<Path> file = Files.list(pathFile).findFirst();
        Map<String, String> metadataMap = new HashMap<>();
        Metadata metadata = ImageMetadataReader.readMetadata(file.get().toFile());
        metadata.getDirectories().forEach(directory ->
                directory.getTags().forEach(tag ->
                        metadataMap.put(tag.getTagName(), tag.getDescription()
                        )));
        return metadataMap;
    }

}
