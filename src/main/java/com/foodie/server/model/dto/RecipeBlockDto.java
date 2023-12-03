package com.foodie.server.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodie.server.model.RecipeBlockType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "block_type", example = "TEXT")
    @JsonProperty("block_type")
    @JsonAlias(value = {"block_type", "type", "blockType"})
    private RecipeBlockType blockType;

    /**
     * if blockType == TEXT then contains block of text
     * if blockType == IMAGE then contains name of image
     */
    @NotBlank(message = "Content can't be blank")
    @Schema(name = "content", example = "This amazing pasta...")
    private String content;

}
