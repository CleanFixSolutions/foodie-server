package com.foodie.server.repository;

import com.foodie.server.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {

    Optional<ImageEntity> findByAuthorAndImageName(String author, String imageName);
}
