package com.foodie.server.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {

    @NotEmpty
    @JsonProperty("recipe_blocks")
    @JsonAlias(value = {"recipe_blocks", "blocks", "list"})
    private List<@Valid RecipeBlockDto> recipeBlocks;

    private String author;

}
