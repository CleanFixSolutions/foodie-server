package com.foodie.server.service;

import com.foodie.server.exception.custom.FIleUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${foodie.storage.images}")
    private String imagePath;

    @Override
    public InputStream downloadImage(String user, String imageName) {
        String path = String.format("%s/%s/%s", imagePath, user, imageName);
        return getClass().getResourceAsStream(path);
    }

    @Override
    public void uploadImage(MultipartFile file, String author) {

        // Check received file
        if (file.isEmpty()) {
            throw new FIleUploadException("Check uploaded file", HttpStatus.BAD_REQUEST);
        }

        // Check if Image directory exist
        Path imageDirectory = Paths.get(System.getProperty("user.dir"), "/src/main/resources", imagePath)
                .toAbsolutePath().normalize();
        File imageFolder = new File(imageDirectory.toString());
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }

        // Check if User directory exist
        Path userFolder = imageDirectory.resolve(author).normalize();
        File uploadUserFolder = new File(userFolder.toString());
        if (!uploadUserFolder.exists()) {
            uploadUserFolder.mkdirs();
        }

        // todo: change file name (hash)
        Path filePath = userFolder.resolve(file.getOriginalFilename()).normalize();
        File destinationFile = filePath.toFile();

        // Check if File in this directory exist
        if (destinationFile.exists()) {
            log.info("This file already exist");
//            throw new FIleUploadException("This file already exist", HttpStatus.BAD_REQUEST);
        }
        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new FIleUploadException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // todo: save meta info in db and return as DTO

    }


}
