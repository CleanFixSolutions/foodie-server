package com.foodie.server.controller;

import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/my")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/recipes")
    public ResponseEntity<List<RecipeDto>> registerUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getRecipesByUsername(authentication.getName()));
    }

}
