package com.foodie.server.controller;

import com.foodie.server.model.dto.UserDto;
import com.foodie.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserDto registerDto) {
        userService.registerUser(registerDto);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDto loginDto) {
        String jwt = userService.login(loginDto);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

}
