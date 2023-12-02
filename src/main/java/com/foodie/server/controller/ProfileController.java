package com.foodie.server.controller;

import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UpdateUserDto;
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

    @GetMapping("/recipes")
    public ResponseEntity<List<RecipeDto>> getRecipes(Authentication authentication) {
        return ResponseEntity.ok(userService.getRecipesByUsername(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<JwtDto> updateProfile(@Valid @RequestBody UpdateUserDto updateUserDto, Authentication authentication) {
        updateUserDto.setOldUsername(authentication.getName());
        return ResponseEntity.ok().body(userService.updateUser(updateUserDto));
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
    public ResponseEntity<Void> delete(Authentication authentication) {
        userService.delete(authentication.getName());
        return ResponseEntity.ok().build();
    }

}
