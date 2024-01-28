package com.foodie.server.controller;

import com.foodie.server.BaseConfigTest;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerRestTemplateTest extends BaseConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Test
    @Order(0)
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
        assertThat(jwtService).isNotNull();
        assertThat(port).isGreaterThan(0);
    }

    private URI getRegisterUrl() {
        return URI.create("http://localhost:" + port + "/api/auth/register");
    }

    private URI getLoginUrl() {
        return URI.create("http://localhost:" + port + "/api/auth/login");
    }

    private URI getRefreshToken() {
        return URI.create("http://localhost:" + port + "/api/auth/refresh-token");
    }

    private final static UserDto USER_DTO = new UserDto("test_user_1", "password");

    @Test
    void register() {
        ResponseEntity<JwtDto> registerExchange = restTemplate.exchange(new RequestEntity<>(USER_DTO, HttpMethod.POST, getRegisterUrl()), JwtDto.class);

        Assertions.assertTrue(registerExchange.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(jwtService.isTokenValid(registerExchange.getBody().getAccessToken(), USER_DTO.getUsername()));
        Assertions.assertTrue(jwtService.isTokenValid(registerExchange.getBody().getRefreshToken(), USER_DTO.getUsername()));
    }

    @Test
    void login() {
        restTemplate.exchange(new RequestEntity<>(USER_DTO, HttpMethod.POST, getRegisterUrl()), JwtDto.class);

        ResponseEntity<JwtDto> loginExchange = restTemplate.exchange(new RequestEntity<>(USER_DTO, HttpMethod.POST, getLoginUrl()), JwtDto.class);

        Assertions.assertTrue(loginExchange.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(jwtService.isTokenValid(loginExchange.getBody().getAccessToken(), USER_DTO.getUsername()));
        Assertions.assertTrue(jwtService.isTokenValid(loginExchange.getBody().getRefreshToken(), USER_DTO.getUsername()));
    }

    @Test
    void refreshToken() {
        ResponseEntity<JwtDto> registerExchange = restTemplate.exchange(new RequestEntity<>(USER_DTO, HttpMethod.POST, getRegisterUrl()), JwtDto.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerExchange.getBody().getRefreshToken()));
        RequestEntity<UserDto> requestEntity = new RequestEntity<>(USER_DTO, httpHeaders, HttpMethod.POST, getRefreshToken());

        ResponseEntity<JwtDto> refreshExchange = restTemplate.exchange(requestEntity, JwtDto.class);

        Assertions.assertTrue(refreshExchange.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(jwtService.isTokenValid(refreshExchange.getBody().getAccessToken(), USER_DTO.getUsername()));
        Assertions.assertTrue(jwtService.isTokenValid(refreshExchange.getBody().getRefreshToken(), USER_DTO.getUsername()));
    }

}
