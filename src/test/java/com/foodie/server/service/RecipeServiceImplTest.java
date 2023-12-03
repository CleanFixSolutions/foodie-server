package com.foodie.server.service;

import com.foodie.server.model.RecipeBlockType;
import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RecipeServiceImplTest {

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
        RecipeDto dto = recipeService.getAllRecipes().get(0);
        Assertions.assertEquals(RECIPE_DTO, dto);
    }
}