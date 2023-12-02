package com.foodie.server.config.security.jwt;

import com.foodie.server.model.dto.JwtDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public interface JwtService {

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(String username);

    String generateRefreshToken(UserDetails userDetails);

    String generateRefreshToken(String username);

    JwtDto refreshTokens(String refreshToken);

    String extractJwt(HttpServletRequest request);

}
