package com.foodie.server.controller;

import com.foodie.server.service.ImageService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping(
            value = "/{user}/{image}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(
            @PathVariable("user") @NotBlank String user,
            @PathVariable("image") @NotBlank String image) {

        InputStream resource = imageService.downloadImage(user, image);
        return resource == null ?
                ResponseEntity.badRequest().build() :
                ResponseEntity.ok().body(new InputStreamResource(resource));
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> postImage(@RequestParam("image") MultipartFile file) {
        // TODO: get Author from JWT
        String author = "default";

        imageService.uploadImage(file, author);

        // TODO: return file name (change file name before and return it)
        return ResponseEntity.ok().build();
    }
}
