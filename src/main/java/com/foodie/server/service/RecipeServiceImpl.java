package com.foodie.server.service;

import com.foodie.server.exception.custom.UserNotFoundClientException;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.entity.RecipeEntity;
import com.foodie.server.model.entity.UserEntity;
import com.foodie.server.repository.RecipeRepository;
import com.foodie.server.repository.UserRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Override
    public void createRecipe(RecipeDto recipeDto) {
        String jsonContext = gson.toJson(recipeDto.getRecipeBlocks());

        UserEntity user = userRepository.findByUsername(recipeDto.getAuthor())
                .orElseThrow(() -> new UserNotFoundClientException(recipeDto.getAuthor()));

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .recipeBlocksJson(jsonContext)
                .user(user)
                .build();
        recipeRepository.save(recipeEntity);
    }

    @Override
    public List<RecipeDto> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, RecipeDto.class))
                .toList();
    }
}
