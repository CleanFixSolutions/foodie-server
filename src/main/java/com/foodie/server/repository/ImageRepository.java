package com.foodie.server.repository;

import com.foodie.server.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    Optional<ImageEntity> findByAuthorAndImageName(String author, String imageName);
}
