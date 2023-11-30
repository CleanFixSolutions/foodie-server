package com.foodie.server.service;

import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {

    JwtDto registerUser(UserDto userDto);

    JwtDto login(UserDto userDto);

    JwtDto refreshToken(HttpServletRequest request, HttpServletResponse response);

    List<RecipeDto> getRecipesByUsername(String username);

    List<UserDto> getUsers();
}
