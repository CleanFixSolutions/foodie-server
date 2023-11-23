package com.foodie.server.service;

import com.foodie.server.model.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private final static UserDto dto = new UserDto("test1@mail.com", "name", "password");

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
}