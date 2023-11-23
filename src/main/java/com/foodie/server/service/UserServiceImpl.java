package com.foodie.server.service;

import com.foodie.server.model.dto.UserDto;
import com.foodie.server.model.entity.UserEntity;
import com.foodie.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void registerUser(UserDto userDto) {
        userRepository.save(modelMapper.map(userDto, UserEntity.class));
    }

    @Override
    public List<UserDto> getUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(e -> modelMapper.map(e, UserDto.class))
                .toList();
    }
}
