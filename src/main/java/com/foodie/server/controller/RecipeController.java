package com.foodie.server.controller;


import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.RecipeResponseDto;
import com.foodie.server.service.RecipeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping()
    public ResponseEntity<RecipeResponseDto> createRecipe(Authentication authentication,
                                                          @Valid @RequestBody RecipeDto recipeDto) {
        recipeDto.setAuthor(authentication.getName());
        return ResponseEntity.ok(recipeService.createRecipe(recipeDto));
    }

    @GetMapping()
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{author}")
    public ResponseEntity<List<RecipeResponseDto>> getAllReceipts(@NotBlank @PathVariable("author") String author) {
        return ResponseEntity.ok(recipeService.getRecipesByUsername(author));
    }

}

