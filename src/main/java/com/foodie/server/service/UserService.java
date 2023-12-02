package com.foodie.server.service;

import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UpdateUserDto;
import com.foodie.server.model.dto.UserDto;

import java.util.List;

public interface UserService {

    JwtDto registerUser(UserDto userDto);

    JwtDto login(UserDto userDto);

    void delete(String username);

    List<RecipeDto> getRecipesByUsername(String username);

    List<UserDto> getUsers();

    JwtDto updateUser(UpdateUserDto updateUserDto);
}
