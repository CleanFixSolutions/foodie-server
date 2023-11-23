package com.foodie.server.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {

    @NotEmpty
    List<@Valid RecipeBlockDto> recipeBlockDtoList;

    String Author;

}
