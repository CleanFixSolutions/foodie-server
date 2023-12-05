package com.foodie.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.RecipeBlockType;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.MethodName.class)
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtService jwtService;

    private static final UserDto USER_DTO = new UserDto("test_user_1", "password");
    private static final UserDto USER_DTO_2 = new UserDto(USER_DTO.getUsername(), "new password");
    private static final RecipeDto RECIPE_DTO = RecipeDto.builder()
            .recipeBlocks(List.of(new RecipeBlockDto(RecipeBlockType.TEXT, "Test text")))
            .author(USER_DTO.getUsername())
            .build();

    private static final String UPDATE_PROFILE_URL = "/api/my";
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String RECIPE_URL = "/api/recipe";
    private static final String DELETE_USER_URL = "/api/my";

    @Test
    void updateProfile() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // update user
        mockMvc.perform(put(UPDATE_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getRefreshToken()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO_2)))
                .andExpect(status().isOk());
    }

    @Test
    void getRecipes() {
        // todo:
    }

    @Test
    void deleteWithoutAuth() throws Exception {
        mockMvc.perform(delete(DELETE_USER_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteWithtAuth() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // delete
        mockMvc.perform(delete(DELETE_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getRefreshToken())))
                .andExpect(status().isOk());
    }

    @Test
    void deleteWithAuthTwice() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // delete
        mockMvc.perform(delete(DELETE_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getRefreshToken())))
                .andExpect(status().isOk());

        // delete second time
        mockMvc.perform(delete(DELETE_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getRefreshToken())))
                .andExpect(status().is4xxClientError());
    }
}