package com.foodie.server.service;

import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.ProfileResponseDto;
import com.foodie.server.model.dto.UserDto;
import com.foodie.server.model.dto.UserUpdateRequestDto;

import java.util.List;

public interface UserService {

    JwtDto registerUser(UserDto userDto);

    JwtDto login(UserDto userDto);

    void delete(String username);

    List<UserDto> getUsers();

    ProfileResponseDto getProfile(String username);

    JwtDto updateUser(UserUpdateRequestDto userUpdateRequestDto);
}
