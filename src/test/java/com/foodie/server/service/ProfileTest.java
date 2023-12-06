package com.foodie.server.service;

import com.foodie.server.model.dto.UserDto;
import com.foodie.server.model.dto.UserUpdateRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProfileTest {

    @Autowired
    private UserService userService;

    private final static UserDto dto = new UserDto("test", "password");
    private final static UserUpdateRequestDto updateUserDto = UserUpdateRequestDto.builder()
            .oldUsername(dto.getUsername())
            .newUsername("new" + dto.getPassword())
            .newPassword("new" + dto.getPassword())
            .build();

    @Test
    void updateUser() {
        userService.registerUser(dto);

        UserDto received1 = userService.getUsers().get(0);
        Assertions.assertEquals(dto.getUsername(), received1.getUsername());

        userService.updateUser(updateUserDto);

        UserDto received2 = userService.getUsers().get(0);
        Assertions.assertNotEquals(dto.getUsername(), received2.getUsername());
        Assertions.assertNotEquals(received1, received2);
        Assertions.assertEquals(updateUserDto.getNewUsername(), received2.getUsername());
    }

    @Test
    void checkCount() {
        Assertions.assertEquals(0, userService.getUsers().size());

        userService.registerUser(dto);
        Assertions.assertEquals(1, userService.getUsers().size());

        userService.updateUser(updateUserDto);
        Assertions.assertEquals(1, userService.getUsers().size());
    }
}
