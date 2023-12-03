package com.foodie.server.config.modelmapper;

import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.entity.RecipeEntity;
import com.google.gson.Gson;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.List;

public class RecipeEntityToRecipeDtoConverter implements Converter<RecipeEntity, RecipeDto> {

    private final Gson gson;

    public RecipeEntityToRecipeDtoConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public RecipeDto convert(MappingContext<RecipeEntity, RecipeDto> context) {
        RecipeEntity entity = context.getSource();
        List<RecipeBlockDto> recipeBlockDtos = List.of(gson.fromJson(entity.getRecipeBlocksJson(), RecipeBlockDto[].class));

        return RecipeDto.builder()
                .author(entity.getUser().getUsername())
                .recipeBlocks(recipeBlockDtos)
                .build();
    }
}
