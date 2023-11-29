package com.foodie.server.service;

import com.foodie.server.model.dto.UserDto;

import java.util.List;

public interface UserService {

    void registerUser(UserDto userDto);

    String login(UserDto userDto);

    List<UserDto> getUsers();

}
