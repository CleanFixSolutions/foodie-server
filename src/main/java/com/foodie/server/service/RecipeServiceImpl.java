package com.foodie.server.service;

import com.foodie.server.exception.custom.RecipeNotFoundClientException;
import com.foodie.server.exception.custom.UserNotFoundClientException;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.RecipeResponseDto;
import com.foodie.server.model.entity.RecipeEntity;
import com.foodie.server.model.entity.UserEntity;
import com.foodie.server.repository.RecipeRepository;
import com.foodie.server.repository.UserRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Override
    public RecipeResponseDto createRecipe(RecipeDto recipeDto) {
        String jsonContext = gson.toJson(recipeDto.getRecipeBlocks());

        UserEntity user = userRepository.findByUsername(recipeDto.getAuthor())
                .orElseThrow(() -> new UserNotFoundClientException(recipeDto.getAuthor()));

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .recipeBlocksJson(jsonContext)
                .user(user)
                .build();
        RecipeEntity saved = recipeRepository.save(recipeEntity);
        return modelMapper.map(saved, RecipeResponseDto.class);
    }

    @Deprecated
    @Override
    public List<RecipeResponseDto> getAllRecipes() {
        return convert(recipeRepository.findAll());
    }

    @Override
    public Page<RecipeResponseDto> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable)
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDto.class));
    }

    @Override
    public List<RecipeResponseDto> getRecipesByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundClientException(username));
        return convert(user.getRecipes());
    }

    @Override
    public RecipeResponseDto getRecipeByUsernameAndId(String username, Long id) {
        Optional<RecipeEntity> recipeEntity = recipeRepository.findById(id);
        if (recipeEntity.isEmpty() || !recipeEntity.get().getUser().getUsername().equals(username)) {
            throw new RecipeNotFoundClientException(username, id);
        }
        return modelMapper.map(recipeEntity.get(), RecipeResponseDto.class);
    }

    @Override
    public void deleteRecipeByUsernameAndId(String username, Long id) {
        Optional<RecipeEntity> recipeEntity = recipeRepository.findById(id);
        if (recipeEntity.isEmpty() || !recipeEntity.get().getUser().getUsername().equals(username)) {
            throw new RecipeNotFoundClientException(username, id);
        }
        recipeRepository.deleteById(id);
    }

    private List<RecipeResponseDto> convert(List<RecipeEntity> recipes) {
        return recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDto.class))
                .toList();
    }
}
