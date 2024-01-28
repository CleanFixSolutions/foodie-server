package com.foodie.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodie.server.BaseConfigTest;
import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.RecipeBlockType;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeBlockDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UserDto;
import com.foodie.server.repository.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class RecipeControllerMockMvcTest extends BaseConfigTest {

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
        mockMvc.perform(get(RECIPE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, jwtService.generateHeader(registerJwt.getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$..author").value(RECIPE_DTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$..recipe_blocks[0].content")
                        .value(RECIPE_DTO.getRecipeBlocks().get(0).getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$..recipe_blocks[0].block_type")
                        .value(RECIPE_DTO.getRecipeBlocks().get(0).getBlockType().name()));
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