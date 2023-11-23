package com.foodie.server.controller;

import com.foodie.server.model.dto.UserDto;
import com.foodie.server.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserDto registerDto) {
        log.info(registerDto.toString());
        registerDto.setPassword(passwordEncoder.encode((registerDto.getPassword())));
        log.info(registerDto.toString());

        userService.registerUser(registerDto);
        return ResponseEntity.ok(null);
    }

}
