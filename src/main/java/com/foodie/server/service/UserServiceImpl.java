package com.foodie.server.service;

import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.exception.custom.UserNotFoundClientException;
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
    public void registerUser(UserDto userDto) {
        UserEntity entity = modelMapper.map(userDto, UserEntity.class);
        entity.setPassword(passwordEncoder.encode((entity.getPassword())));
        userRepository.save(entity);
    }

    @Override
    public String login(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        UserEntity user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new UserNotFoundClientException(userDto.getUsername()));
        final String jwtToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        // todo: return jwtDto
        return jwtToken;
    }

    @Override
    public List<UserDto> getUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(e -> modelMapper.map(e, UserDto.class))
                .toList();
    }
}
