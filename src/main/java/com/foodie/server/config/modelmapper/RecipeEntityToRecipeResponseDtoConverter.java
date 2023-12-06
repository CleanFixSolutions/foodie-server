package com.foodie.server.config.modelmapper;

import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeResponseDto;
import com.foodie.server.model.entity.RecipeEntity;
import com.google.gson.Gson;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.List;

public class RecipeEntityToRecipeResponseDtoConverter implements Converter<RecipeEntity, RecipeResponseDto> {

    private final Gson gson;

    public RecipeEntityToRecipeResponseDtoConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public RecipeResponseDto convert(MappingContext<RecipeEntity, RecipeResponseDto> context) {
        RecipeEntity entity = context.getSource();
        List<RecipeBlockDto> recipeBlockDtos = List.of(gson.fromJson(entity.getRecipeBlocksJson(), RecipeBlockDto[].class));

        return RecipeResponseDto.builder()
                .id(entity.getId())
                .author(entity.getUser().getUsername())
                .recipeBlocks(recipeBlockDtos)
                .creationTime(entity.getCreationTime())
                .lastModificationTime(entity.getLastModificationTime())
                .build();
    }
}
