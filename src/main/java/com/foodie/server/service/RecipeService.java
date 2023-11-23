package com.foodie.server.service;

import com.foodie.server.model.dto.RecipeDto;

import java.util.List;

public interface RecipeService {

    void createRecipe(RecipeDto recipeDto);

    List<RecipeDto> getAllRecipes();

//    List<RecipeEntity> getAllReceiptsByAuthor(String Author);

}