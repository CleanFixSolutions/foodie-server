package com.foodie.server.service;

import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.RecipeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    RecipeResponseDto createRecipe(RecipeDto recipeDto);

    List<RecipeResponseDto> getAllRecipes();

    Page<RecipeResponseDto> getAllRecipes(Pageable pageable);

    List<RecipeResponseDto> getRecipesByUsername(String username);

    RecipeResponseDto getRecipeByUsernameAndId(String username, Long id);

    void deleteRecipeByUsernameAndId(String username, Long id);
}
