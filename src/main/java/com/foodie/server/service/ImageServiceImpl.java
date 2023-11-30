package com.foodie.server.service;

import com.foodie.server.exception.custom.FileUploadClientException;
import com.foodie.server.model.entity.ImageEntity;
import com.foodie.server.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @SneakyThrows
    @Override
    public byte[] downloadImage(String user, String imageName) {
        Optional<ImageEntity> imageEntity = imageRepository.findByAuthorAndImageName(user, imageName);
        return Files.readAllBytes(Path.of(imageEntity.get().getPath()));
    }

    @SneakyThrows
    @Override
    public String uploadImage(MultipartFile file, String author) {

        // Check received file
        if (file.isEmpty()) {
            throw new FileUploadClientException("Uploaded file is empty");
        }

        // Check if Image directory exist
        Path imageDirectory = Paths.get(System.getProperty("user.dir"), "images").toAbsolutePath().normalize();
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

        // change file name (hash)
        final String originalFilename = file.getOriginalFilename();
        final String newImageName =
                Arrays.hashCode(file.getBytes()) + originalFilename.substring(originalFilename.lastIndexOf('.'));

        log.info("new image name=" + newImageName);

        Path filePath = userFolder.resolve(newImageName).normalize();
        imageRepository.save(ImageEntity.builder()
                .author(author)
                .path(filePath.toString())
                .imageName(newImageName)
                .uploadTime(Date.from(Instant.now()))
                .build());

        File destinationFile = filePath.toFile();

        // Check if File in this directory exist
        if (destinationFile.exists()) {
            log.info("This file already exist");
        }
        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new FileUploadClientException(e.getMessage());
        }

        // todo: return dto?
        return newImageName;
    }

}
