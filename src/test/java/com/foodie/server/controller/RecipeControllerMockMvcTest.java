package com.foodie.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.RecipeBlockType;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UserDto;
import com.foodie.server.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.MethodName.class)
@AutoConfigureMockMvc
class RecipeControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private JwtService jwtService;

    private static final String DELETE_USER_URL = "/api/my";
    private static final UserDto USER_DTO = new UserDto("test_user_1", "password");
    private static final RecipeDto RECIPE_DTO = RecipeDto.builder()
            .recipeBlocks(List.of(new RecipeBlockDto(RecipeBlockType.TEXT, "Test text")))
            .author(USER_DTO.getUsername())
            .build();
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String RECIPE_URL = "/api/recipe";

    @Test
    @Order(0)
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(recipeRepository).isNotNull();
        assertThat(jwtService).isNotNull();
    }

    @Test
    void postRecipe() throws Exception {
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
    }

    @Test
    void postRecipeWithoutAuth() throws Exception {
        // post recipe
        mockMvc.perform(post(RECIPE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_DTO)))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void getRecipesWithoutAuth() throws Exception {
        mockMvc.perform(get(RECIPE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getAllRecipesWithAuth() throws Exception {
        // register
        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(USER_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JwtDto registerJwt = mapper.readValue(registerResult.getResponse().getContentAsString(), JwtDto.class);

        // get recipes
        mockMvc.perform(get(RECIPE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRecipes() throws Exception {
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

        // get recipes
        MvcResult getResult = mockMvc.perform(get(RECIPE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk())
                .andReturn();

        RecipeDto[] receivedRecipes = mapper.readValue(getResult.getResponse().getContentAsString(), RecipeDto[].class);
        Assertions.assertEquals(1, receivedRecipes.length);
        Assertions.assertEquals(RECIPE_DTO, receivedRecipes[0]);
    }

    @Test
    void authAfterDeleteUser() throws Exception {
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

        // post recipe
        mockMvc.perform(post(RECIPE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RECIPE_DTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().is4xxClientError());

        // get recipes
        mockMvc.perform(get(RECIPE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteUserAndRecipesCascade() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken()))
                        .content(mapper.writeValueAsString(RECIPE_DTO)))
                .andExpect(status().isOk());

        // check that repository isn't empty
        Assertions.assertFalse(recipeRepository.findAll().isEmpty());

        // delete
        mockMvc.perform(delete(DELETE_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk());

        // check that repository is empty
        Assertions.assertTrue(recipeRepository.findAll().isEmpty());
    }
}