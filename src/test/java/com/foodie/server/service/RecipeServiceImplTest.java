package com.foodie.server.service;

import com.foodie.server.BaseConfigTest;
import com.foodie.server.model.RecipeBlockType;
import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.RecipeResponseDto;
import com.foodie.server.model.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Date;
import java.util.List;

class RecipeServiceImplTest extends BaseConfigTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    private final static UserDto USER_DTO = new UserDto("name", "password");
    private static final RecipeBlockDto recipeBlock = new RecipeBlockDto(RecipeBlockType.TEXT, "Text");
    private static final RecipeDto RECIPE_DTO = RecipeDto.builder()
            .recipeBlocks(List.of(recipeBlock))
            .author(USER_DTO.getUsername())
            .build();

    @BeforeEach
    void init() {
        userService.registerUser(USER_DTO);
    }

    @Test
    void createRecipe() {
        Assertions.assertEquals(0, recipeService.getAllRecipes().size());
        recipeService.createRecipe(RECIPE_DTO);
        Assertions.assertEquals(1, recipeService.getAllRecipes().size());
    }

    @Test
    void checkSavedContent() {
        recipeService.createRecipe(RECIPE_DTO);
        RecipeResponseDto dto = recipeService.getAllRecipes().get(0);
        Assertions.assertEquals(RECIPE_DTO.getAuthor(), dto.getAuthor());
        Assertions.assertEquals(RECIPE_DTO.getRecipeBlocks(), dto.getRecipeBlocks());
    }

    @Test
    void getRecipeByUsernameAndId() {
        RecipeResponseDto createDto = recipeService.createRecipe(RECIPE_DTO);
        Assertions.assertEquals(RECIPE_DTO.getAuthor(), createDto.getAuthor());
        Assertions.assertEquals(RECIPE_DTO.getRecipeBlocks(), createDto.getRecipeBlocks());

        RecipeResponseDto getDto = recipeService.getRecipeByUsernameAndId(RECIPE_DTO.getAuthor(), 1L);
        Assertions.assertEquals(RECIPE_DTO.getAuthor(), getDto.getAuthor());
        Assertions.assertEquals(RECIPE_DTO.getRecipeBlocks(), getDto.getRecipeBlocks());
        Assertions.assertTrue(getDto.getCreationTime().before(Date.from(Instant.now())));
        Assertions.assertTrue(getDto.getLastModificationTime().before(Date.from(Instant.now())));
        Assertions.assertTrue(getDto.getId() > 0);
    }

    @Test
    void deleteRecipeByUsernameAndId() {
        Assertions.assertEquals(0, recipeService.getAllRecipes().size());
        RecipeResponseDto createDto = recipeService.createRecipe(RECIPE_DTO);
        Assertions.assertEquals(1, recipeService.getAllRecipes().size());
        recipeService.deleteRecipeByUsernameAndId(createDto.getAuthor(), createDto.getId());
        Assertions.assertEquals(0, recipeService.getAllRecipes().size());
    }

    @Test
    void getAllRecipesPageable1() {
        Page<RecipeResponseDto> emptyRecipes = recipeService.getAllRecipes(PageRequest.of(0, 5));
        Assertions.assertEquals(0, emptyRecipes.getTotalPages());
        Assertions.assertEquals(0, emptyRecipes.getTotalElements());

        RecipeResponseDto createDto = recipeService.createRecipe(RECIPE_DTO);
        Assertions.assertEquals(1, recipeService.getAllRecipes().size());

        Page<RecipeResponseDto> allRecipes = recipeService.getAllRecipes(PageRequest.of(0, 5));
        Assertions.assertEquals(1, allRecipes.getTotalPages());
        Assertions.assertEquals(1, allRecipes.getTotalElements());
        Assertions.assertEquals(createDto, allRecipes.getContent().get(0));
    }

    @Test
    void getAllRecipesPageable3() {
        Assertions.assertEquals(0, recipeService.getAllRecipes().size());
        recipeService.createRecipe(RECIPE_DTO);
        recipeService.createRecipe(RECIPE_DTO);
        recipeService.createRecipe(RECIPE_DTO);
        Assertions.assertEquals(3, recipeService.getAllRecipes().size());

        Page<RecipeResponseDto> allRecipes = recipeService.getAllRecipes(PageRequest.of(0, 5));
        Assertions.assertEquals(1, allRecipes.getTotalPages());
        Assertions.assertEquals(3, allRecipes.getTotalElements());
    }

}