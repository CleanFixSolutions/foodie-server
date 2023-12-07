package com.foodie.server.service;

import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.RecipeResponseDto;

import java.util.List;

public interface RecipeService {

    RecipeResponseDto createRecipe(RecipeDto recipeDto);

    List<RecipeResponseDto> getAllRecipes();

    List<RecipeDto> getRecipesByUsername(String username);

    RecipeResponseDto getRecipeByUsernameAndId(String username, Long id);

    void deleteRecipeByUsernameAndId(String username, Long id);
}
