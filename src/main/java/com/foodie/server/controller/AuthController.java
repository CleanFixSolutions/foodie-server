package com.foodie.server.controller;

import com.foodie.server.config.security.jwt.JwtService;
import com.foodie.server.model.dto.JwtDto;
import com.foodie.server.model.dto.UserDto;
import com.foodie.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<JwtDto> registerUser(@Valid @RequestBody UserDto registerDto) {
        return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody UserDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<JwtDto> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
        return ResponseEntity.ok(jwtService.refreshTokens(refreshToken));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("hello world!");
    }
}
