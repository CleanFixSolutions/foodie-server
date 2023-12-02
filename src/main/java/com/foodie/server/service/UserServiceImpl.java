package com.foodie.server.service;

import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.exception.custom.EmptyUserUpdateException;
import com.foodie.server.exception.custom.UserNotFoundClientException;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.RecipeDto;
import com.foodie.server.model.dto.UpdateUserDto;
import com.foodie.server.model.dto.UserDto;
import com.foodie.server.model.entity.UserEntity;
import com.foodie.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public JwtDto registerUser(UserDto userDto) {
        UserEntity entity = modelMapper.map(userDto, UserEntity.class);
        entity.setPassword(passwordEncoder.encode((entity.getPassword())));
        userRepository.save(entity);
        return JwtDto.builder()
                .accessToken(jwtService.generateAccessToken(entity.getUsername()))
                .refreshToken(jwtService.generateRefreshToken(entity.getUsername()))
                .build();
    }

    @Override
    public JwtDto login(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        return JwtDto.builder()
                .accessToken(jwtService.generateAccessToken(userDto.getUsername()))
                .refreshToken(jwtService.generateRefreshToken(userDto.getUsername()))
                .build();
    }

    @Override
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    public List<RecipeDto> getRecipesByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundClientException(username));
        return user.getRecipes().stream()
                .map(recipe -> modelMapper.map(recipe, RecipeDto.class))
                .toList();
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(e -> modelMapper.map(e, UserDto.class))
                .toList();
    }

    @Override
    public JwtDto updateUser(UpdateUserDto updateUserDto) {
        if (!updateUserDto.isUpdated()) {
            throw new EmptyUserUpdateException("Nothing new to update");
        }
        log.info(updateUserDto.toString());
        UserEntity user = userRepository.findByUsername(updateUserDto.getOldUsername())
                .orElseThrow(() -> new UserNotFoundClientException(updateUserDto.getOldUsername()));

        if (updateUserDto.getNewUsername() != null){
            user.setUsername(updateUserDto.getNewUsername());
        }
        if (updateUserDto.getNewPassword() != null){
            user.setPassword(passwordEncoder.encode((updateUserDto.getNewPassword())));
        }
        userRepository.save(user);
        return JwtDto.builder()
                .accessToken(jwtService.generateAccessToken(user.getUsername()))
                .refreshToken(jwtService.generateRefreshToken(user.getUsername()))
                .build();
    }
}
