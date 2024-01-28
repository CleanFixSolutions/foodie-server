package com.foodie.server.service;

import com.foodie.server.BaseConfigTest;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceImplTest extends BaseConfigTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    private final static UserDto dto = new UserDto("name", "password");

    @Test
    void registerUser() {
        Assertions.assertEquals(0, userService.getUsers().size());
        userService.registerUser(dto);
        Assertions.assertEquals(1, userService.getUsers().size());
    }

    @Test
    void registerUserTwice() {
        Assertions.assertEquals(0, userService.getUsers().size());
        userService.registerUser(dto);
        Assertions.assertEquals(1, userService.getUsers().size());
        Assertions.assertThrows(Exception.class, () -> userService.registerUser(dto));
        Assertions.assertEquals(1, userService.getUsers().size());
    }

    @Test
    void register2User() {
        Assertions.assertEquals(0, userService.getUsers().size());
        userService.registerUser(new UserDto("name 1", "password 1"));
        userService.registerUser(new UserDto("name 2", "password 2"));
        Assertions.assertEquals(2, userService.getUsers().size());
    }

    @Test
    void registerAndDeleteUser() {
        userService.registerUser(dto);
        Assertions.assertEquals(1, userService.getUsers().size());
        userService.delete(dto.getUsername());
        Assertions.assertEquals(0, userService.getUsers().size());
    }

    @Test
    void checkUsernameFromRegistrationToken() {
        JwtDto jwtDto = userService.registerUser(dto);
        Assertions.assertEquals(dto.getUsername(), jwtService.extractUsername(jwtDto.getAccessToken()));
        Assertions.assertEquals(dto.getUsername(), jwtService.extractUsername(jwtDto.getRefreshToken()));
    }

    @Test
    void checkUsernameFromAuthToken() {
        userService.registerUser(dto);
        JwtDto jwtDto = userService.login(dto);
        Assertions.assertEquals(dto.getUsername(), jwtService.extractUsername(jwtDto.getAccessToken()));
        Assertions.assertEquals(dto.getUsername(), jwtService.extractUsername(jwtDto.getRefreshToken()));
    }
}