package com.foodie.server.config.security.jwt;

import com.foodie.server.exception.custom.JwtNotFoundException;
import com.foodie.server.model.dto.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtServiceImpl implements JwtService {

    @Value("${foodie.jwt.expiration}")
    private long JWT_EXPIRATION;

    @Value("${foodie.jwt.refresh-expiration}")
    private long JWT_REFRESH_EXPIRATION;

    @Value("${foodie.jwt.secret}")
    private String JWT_SECRET;

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Deprecated
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    @Deprecated
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(userDetails.getUsername());
    }

    @Override
    public String generateToken(String username) {
        return buildToken(username, JWT_EXPIRATION);

    }

    @Override
    public String generateRefreshToken(String username) {
        return buildToken(username, JWT_REFRESH_EXPIRATION);
    }

    @Override
    public JwtDto refreshTokens(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new JwtNotFoundException();
        }
        final String jwt = refreshToken.substring(7);
        final String username = extractUsername(jwt);

        return JwtDto.builder()
                .accessToken(generateToken(username))
                .refreshToken(generateRefreshToken(username))
                .build();
    }

    public String extractJwt(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

//    private String buildToken(UserDetails userDetails, long expiration) {
//        String username = userDetails.getUsername();
//        Date currentDate = Date.from(Instant.now());
//        Date expireDate = Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS));
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(currentDate)
//                .setExpiration(expireDate)
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }

    private String buildToken(final String username, final long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }
}
