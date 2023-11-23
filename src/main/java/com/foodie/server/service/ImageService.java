package com.foodie.server.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageService {

    void uploadImage(MultipartFile file, String author);

    InputStream downloadImage(@NotBlank String user, @NotBlank String imageName);
}
