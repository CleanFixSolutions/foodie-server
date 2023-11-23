package com.foodie.server.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("api/")
public class ImageController {

    @Value("${foodie.storage.images}")
    private String imagePath;

    @GetMapping("/images/{user}/{image}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(
            @PathVariable("user") @NotBlank String user,
            @PathVariable("image") @NotBlank String image) {

        String path = String.format("%s/%s/%s", imagePath, user, image);
        InputStream resource = getClass().getResourceAsStream(path);
        if (resource == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new InputStreamResource(resource));
        }
    }
}
