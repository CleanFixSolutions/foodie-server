package com.foodie.server.service;

import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.entity.RecipeEntity;
import com.foodie.server.repository.RecipeRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    RecipeRepository recipeRepository;

    private final Gson gson = new Gson();

    @Override
    public void createRecipe(RecipeDto recipeDto) {
        String jsonContext = gson.toJson(recipeDto.getRecipeBlockDtoList());
        RecipeEntity recipeEntity = new RecipeEntity(jsonContext, recipeDto.getAuthor());
        recipeRepository.save(recipeEntity);
    }

    @Override
    public List<RecipeDto> getAllRecipes() {
        List<RecipeDto> list = new ArrayList<>();
        for (var i : recipeRepository.findAll()) {
            RecipeBlockDto[] recipeBlockDtos = gson.fromJson(i.getRecipeBlocksJson(), RecipeBlockDto[].class);
            list.add(new RecipeDto(List.of(recipeBlockDtos), i.getAuthor()));
        }
        return list;
    }
}
