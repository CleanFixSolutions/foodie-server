package com.foodie.server.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipeJson")
    private String recipeBlocksJson;

//    @ManyToOne
//    UserEntity userEntity;

    @Column(name = "author")
    private String author;

    public RecipeEntity(String recipeBlocksJson, String author) {
        this.recipeBlocksJson = recipeBlocksJson;
        this.author = author;
    }
}
