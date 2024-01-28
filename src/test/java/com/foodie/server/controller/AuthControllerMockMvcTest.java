package com.foodie.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodie.server.BaseConfigTest;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerMockMvcTest extends BaseConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtService jwtService;

    private static final UserDto USER_DTO = new UserDto("test_user_1", "password");
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String REFRESH_TOKEN_URL = "/api/auth/refresh-token";

    @Test
    @Order(0)
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(jwtService).isNotNull();
    }

    @Deprecated
    @Test
    void testMock() throws Exception {
        this.mockMvc.perform(get("/api/auth/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hello world!")));
    }

    @Test
    void register() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "test_user_1",
                                    "password":"password"
                                }"""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("access_token")))
                .andExpect(content().string(containsString("refresh_token")));
    }

    @Test
    void registerDeserializeResponseAndValidateTokens() throws Exception {
        MvcResult response = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andReturn();

        JwtDto jwtDto = mapper.readValue(response.getResponse().getContentAsString(), JwtDto.class);

        Assertions.assertTrue(jwtService.isTokenValid(jwtDto.getAccessToken(), USER_DTO.getUsername()));
        Assertions.assertTrue(jwtService.isTokenValid(jwtDto.getRefreshToken(), USER_DTO.getUsername()));
    }


    @Test
    void login() throws Exception {
        // register
        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(USER_DTO)));
        // login
        MvcResult response = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JwtDto jwtDto = mapper.readValue(response.getResponse().getContentAsString(), JwtDto.class);

        Assertions.assertTrue(jwtService.isTokenValid(jwtDto.getAccessToken(), USER_DTO.getUsername()));
        Assertions.assertTrue(jwtService.isTokenValid(jwtDto.getRefreshToken(), USER_DTO.getUsername()));
    }

    @Test
    void refreshToken() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // refresh
        MvcResult refreshResult = mockMvc.perform(post(REFRESH_TOKEN_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getRefreshToken())))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto refreshJwt = mapper.readValue(refreshResult.getResponse().getContentAsString(), JwtDto.class);
        Assertions.assertTrue(jwtService.isTokenValid(refreshJwt.getAccessToken(), USER_DTO.getUsername()));
        Assertions.assertTrue(jwtService.isTokenValid(refreshJwt.getRefreshToken(), USER_DTO.getUsername()));
    }
}