package com.foodie.server.model.dto;

import com.foodie.server.model.RecipeBlockType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeBlockDto {

    @NotNull(message = "Should be TEXT or IMAGE")
    private RecipeBlockType blockType;

    /**
     * if blockType == TEXT then contains block of text
     * if blockType == IMAGE then contains name of image
     */
    @NotBlank(message = "Content can't be blank")
    private String content;

}
