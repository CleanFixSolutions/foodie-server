package com.foodie.server.controller;

import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.ProfileResponseDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UserUpdateRequestDto;
import com.foodie.server.service.RecipeService;
import com.foodie.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/my")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final RecipeService recipeService;

    @GetMapping("/recipes")
    public ResponseEntity<List<RecipeDto>> getMyRecipes(Authentication authentication) {
        return ResponseEntity.ok(recipeService.getRecipesByUsername(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<ProfileResponseDto> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<JwtDto> updateProfile(@Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto, Authentication authentication) {
        userUpdateRequestDto.setOldUsername(authentication.getName());
        return ResponseEntity.ok().body(userService.updateUser(userUpdateRequestDto));
    }

    /**
     * Handles HTTP DELETE requests to delete a user account.
     * <p>
     * This method requires the user to be authenticated, and the Authentication parameter
     * provides access to information about the authenticated user, including their username.
     * <p>
     * Note: If the user tries to send a DELETE request to themselves a second time,
     * they receive a 403 error because they cannot pass authentication due to not being in the database
     *
     * @param authentication The Authentication object containing details about the authenticated user.
     * @return ResponseEntity with HTTP status 200 (OK) if the user account deletion is successful.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMyProfile(Authentication authentication) {
        userService.delete(authentication.getName());
        return ResponseEntity.ok().build();
    }

}
