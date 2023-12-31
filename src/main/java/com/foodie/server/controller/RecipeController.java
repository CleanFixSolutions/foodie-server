package com.foodie.server.controller;


import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.service.RecipeService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> createRecipe(@Valid @RequestBody RecipeDto recipeDto, Authentication authentication) {
        recipeDto.setAuthor(authentication.getName());
        recipeService.createRecipe(recipeDto);
        return ResponseEntity.ok(null);
    }

    @GetMapping()
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

//    @GetMapping("/{author}")
//    public ResponseEntity<List<RecipeDto>> getAllReceipts(@PathVariable("author") @NotBlank String author) {
//        return ResponseEntity.ok(recipeService.getAllReceiptsByAuthor(author));
//    }

}

