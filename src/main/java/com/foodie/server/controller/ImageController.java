package com.foodie.server.controller;

import com.foodie.server.service.ImageService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping(
            value = "/{user}/{image}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    @ResponseBody
    public ResponseEntity<byte[]> getImage(
            @PathVariable("user") @NotBlank String user,
            @PathVariable("image") @NotBlank String image) {
        byte[] resource = imageService.downloadImage(user, image);
        return resource == null ?
                ResponseEntity.badRequest().build() :
                ResponseEntity.ok().body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> postImage(@RequestParam("image") MultipartFile file, Authentication authentication) {
        // todo: return dto?
        return ResponseEntity.ok().body(imageService.uploadImage(file, authentication.getName()));
    }
}
