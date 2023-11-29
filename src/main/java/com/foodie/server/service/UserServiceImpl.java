package com.foodie.server.service;

import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.exception.custom.JwtNotFoundException;
import com.foodie.server.exception.custom.UserNotFoundClientException;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.UserDto;
import com.foodie.server.model.entity.UserEntity;
import com.foodie.server.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
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
                .accessToken(jwtService.generateToken(entity))
                .refreshToken(jwtService.generateRefreshToken(entity))
                .build();
    }

    @Override
    public JwtDto login(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        UserEntity user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new UserNotFoundClientException(userDto.getUsername()));
        return JwtDto.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    @Override
    public JwtDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String jwt = jwtService.extractJwt(request);
        if (jwt == null) {
            throw new JwtNotFoundException();
        }
        final String username = jwtService.extractUsername(jwt);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundClientException(username));

        return JwtDto.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    @Override
    public List<UserDto> getUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(e -> modelMapper.map(e, UserDto.class))
                .toList();
    }
}
