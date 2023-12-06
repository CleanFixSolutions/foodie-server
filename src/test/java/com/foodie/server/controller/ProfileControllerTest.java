package com.foodie.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.RecipeBlockType;
import com.foodie.server.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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

import java.time.Instant;
import java.util.Date;
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
    private static final UserDto USER_DTO_2 = new UserDto("test_user_2", "new password");
    private static final RecipeDto RECIPE_DTO = RecipeDto.builder()
            .recipeBlocks(List.of(new RecipeBlockDto(RecipeBlockType.TEXT, "Test text")))
            .author(USER_DTO.getUsername())
            .build();

    private static final String UPDATE_PROFILE_URL = "/api/my";
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String RECIPE_URL = "/api/recipe";
    private static final String DELETE_USER_URL = "/api/my";
    private static final String GET_PROFILE_URL = "/api/my";

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
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO_2)))
                .andExpect(status().isOk());
    }

    @Test
    void getPersonalRecipes() throws Exception  {
        // register user_1
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();
        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // register user_2
        MvcResult registerResult2 = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO_2)))
                .andExpect(status().isOk())
                .andReturn();
        JwtDto registerJwt2 = mapper.readValue(registerResult2.getResponse().getContentAsString(), JwtDto.class);

        // user_1 post recipe
        mockMvc.perform(post(RECIPE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_DTO)))
                .andExpect(status().isOk());

        // check user 1
        MvcResult getUserProfile = mockMvc.perform(get(GET_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk())
                .andReturn();
        ProfileResponseDto profileResponseDto = mapper.readValue(getUserProfile.getResponse().getContentAsString(), ProfileResponseDto.class);
        Assertions.assertEquals(USER_DTO.getUsername(), profileResponseDto.getUsername());
        Assertions.assertEquals(1, profileResponseDto.getRecipes().size());

        // check user 2
        MvcResult getUserProfile2 = mockMvc.perform(get(GET_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt2.getAccessToken())))
                .andExpect(status().isOk())
                .andReturn();
        ProfileResponseDto profileResponseDto2 = mapper.readValue(getUserProfile2.getResponse().getContentAsString(), ProfileResponseDto.class);
        Assertions.assertEquals(USER_DTO_2.getUsername(), profileResponseDto2.getUsername());
        Assertions.assertTrue( profileResponseDto2.getRecipes().isEmpty());
    }

    @Test
    void getProfileWithoutAuth() throws Exception {
        mockMvc.perform(get(GET_PROFILE_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getProfile() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        MvcResult getResult = mockMvc.perform(get(GET_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk())
                .andReturn();

        ProfileResponseDto profileResponseDto = mapper.readValue(getResult.getResponse().getContentAsString(), ProfileResponseDto.class);
        Assertions.assertEquals(USER_DTO.getUsername(), profileResponseDto.getUsername());
        Assertions.assertTrue(profileResponseDto.getCreationTime().before(Date.from(Instant.now())));
        Assertions.assertTrue(profileResponseDto.getLastModificationTime().before(Date.from(Instant.now())));
        Assertions.assertTrue(profileResponseDto.getRecipes().isEmpty());
    }

    @Test
    void getProfileWithRecipes() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // post recipe
        mockMvc.perform(post(RECIPE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_DTO)))
                .andExpect(status().isOk());

        MvcResult getResult = mockMvc.perform(get(GET_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk())
                .andReturn();

        ProfileResponseDto profileResponseDto = mapper.readValue(getResult.getResponse().getContentAsString(), ProfileResponseDto.class);
        Assertions.assertEquals(USER_DTO.getUsername(), profileResponseDto.getUsername());
        Assertions.assertTrue(profileResponseDto.getCreationTime().before(Date.from(Instant.now())));
        Assertions.assertTrue(profileResponseDto.getLastModificationTime().before(Date.from(Instant.now())));
        Assertions.assertEquals(1, profileResponseDto.getRecipes().size());
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
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
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
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk());

        // delete second time
        mockMvc.perform(delete(DELETE_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().is4xxClientError());
    }
}