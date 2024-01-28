package com.foodie.server.config.security.jwt;

import com.foodie.server.BaseConfigTest;
import com.foodie.server.model.dto.JwtDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JwtServiceTest extends BaseConfigTest {

    @Autowired
    private JwtService jwtService;

    private static final String USERNAME_1 = "TEST_USER";

    @Test
    void generateToken() {
        String token = jwtService.generateAccessToken(USERNAME_1);
        String jwtUser = jwtService.extractUsername(token);
        Assertions.assertEquals(USERNAME_1, jwtUser);
    }

    @Test
    void validateAccessToken() {
        String token = jwtService.generateAccessToken(USERNAME_1);
        Assertions.assertTrue(jwtService.isTokenValid(token, USERNAME_1));
        Assertions.assertFalse(jwtService.isTokenValid(token, USERNAME_1 + "text"));
    }

    @Test
    void validateRefreshToken() {
        String token = jwtService.generateRefreshToken(USERNAME_1);
        Assertions.assertTrue(jwtService.isTokenValid(token, USERNAME_1));
        Assertions.assertFalse(jwtService.isTokenValid(token, USERNAME_1 + "text"));
    }

    @Test
    void refreshTokens() {
        String refreshToken = jwtService.generateRefreshToken(USERNAME_1);
        JwtDto refreshedTokens = jwtService.refreshTokens(jwtService.generateHeader(refreshToken));

        Assertions.assertTrue(jwtService.isTokenValid(refreshedTokens.getAccessToken(), USERNAME_1));
        Assertions.assertFalse(jwtService.isTokenValid(refreshedTokens.getAccessToken(), USERNAME_1 + "text"));
    }

    @Test
    void extractJwtFromHeader() {
        final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJURVNUX1VTRVIiLCJpYXQiOjE3MDE1MTE1MTgsImV4cCI6MTcwMTU5NzkxOH0._-BD2fu5woTLfyny8og5R4ZEFgpuymu4l1_fBgjhXe4";
        final String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJURVNUX1VTRVIiLCJpYXQiOjE3MDE1MTE1MTgsImV4cCI6MTcwMjExNjMxOH0.YR1cC6CzyRwnAaxjTHStcLyz7J6dflrVP0ffuBYCrAQ";

        Assertions.assertEquals(accessToken, jwtService.extractJwt(jwtService.generateHeader(accessToken)));
        Assertions.assertEquals(refreshToken, jwtService.extractJwt(jwtService.generateHeader(refreshToken)));
    }

    @Test
    void extractGeneratedJwtFromHeader() {
        String accessToken = jwtService.generateAccessToken(USERNAME_1);
        String refreshToken = jwtService.generateRefreshToken(USERNAME_1);

        Assertions.assertEquals(accessToken, jwtService.extractJwt(jwtService.generateHeader(accessToken)));
        Assertions.assertEquals(refreshToken, jwtService.extractJwt(jwtService.generateHeader(refreshToken)));
    }

    @Test
    void extractUsernameFromJwt() {
        String accessToken = jwtService.generateAccessToken(USERNAME_1);
        String refreshToken = jwtService.generateRefreshToken(USERNAME_1);

        Assertions.assertEquals(USERNAME_1, jwtService.extractUsername(accessToken));
        Assertions.assertEquals(USERNAME_1, jwtService.extractUsername(refreshToken));
    }

    @Test
    void generateHeader() {
        String accessToken = jwtService.generateAccessToken(USERNAME_1);
        String refreshToken = jwtService.generateRefreshToken(USERNAME_1);
        Assertions.assertEquals("Bearer " + accessToken, jwtService.generateHeader(accessToken));
        Assertions.assertEquals("Bearer " + refreshToken, jwtService.generateHeader(refreshToken));
    }
}