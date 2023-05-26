package com.rest.demoapi.controller;

import com.rest.demoapi.model.ImageDto;
import com.rest.demoapi.service.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private IImageService service;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ImageDto> upload(@RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.of(service.save(multipartFile));
    }

    @GetMapping(value = "/download/{name}")
    public ResponseEntity<?> download(@PathVariable("name") String name) {
        Optional<Resource> resource = service.download(name);

        String headerValue = "attachment; filename=\"" + resource.get().getFilename() + "\"";
        String contentType = "application/octet-stream";
        return resource.map(value -> ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource.get())).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<ImageDto> getImage(@PathVariable("id") String id) {
        Optional<ImageDto> image = service.find(id);
        return image.map(value -> ResponseEntity.ok()
                .body(image.get())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<ImageDto>> getImages() {
        return ResponseEntity.ok()
                .body(service.findAll());
    }

}
