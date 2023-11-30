package com.foodie.server.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String uploadImage(MultipartFile file, String author);

    byte[] downloadImage(@NotBlank String user, @NotBlank String imageName);
}
