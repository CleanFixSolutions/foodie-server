package com.foodie.server.config.security.jwt;

import com.foodie.server.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private final static UserEntity USER_ENTITY = UserEntity.builder()
            .username("TEST_USER")
            .password("TEST_PASSWORD")
            .build();

    @Test
    void generateToken() {
        String token = jwtService.generateToken(USER_ENTITY);
        String jwtUser = jwtService.extractUsername(token);
        Assertions.assertEquals(USER_ENTITY.getUsername(), jwtUser);
    }
}