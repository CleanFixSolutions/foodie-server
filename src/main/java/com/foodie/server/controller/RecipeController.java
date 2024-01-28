package com.foodie.server.controller;


import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.RecipeResponseDto;
import com.foodie.server.service.RecipeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<Page<RecipeResponseDto>> getAllRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(recipeService.getAllRecipes(pageRequest));
    }

    @GetMapping("/{author}")
    public ResponseEntity<List<RecipeResponseDto>> getAllReceiptsByAuthor(@NotBlank @PathVariable("author") String author) {
        return ResponseEntity.ok(recipeService.getRecipesByUsername(author));
    }
}

