package com.foodie.server.config.security.jwt;

import com.foodie.server.exception.custom.JwtNotFoundException;
import com.foodie.server.model.dto.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public boolean isTokenValid(String token, String expectedUsername) {
        return extractUsername(token).equals(expectedUsername) && !extractExpiration(token).before(new Date());
    }

    @Override
    public String generateAccessToken(String username) {
        return buildToken(username, JWT_EXPIRATION);
    }

    @Override
    public String generateRefreshToken(String username) {
        return buildToken(username, JWT_REFRESH_EXPIRATION);
    }

    @Override
    public JwtDto refreshTokens(String authorizationHeader) {
        final String jwt = extractJwt(authorizationHeader);
        if (jwt == null) {
            throw new JwtNotFoundException();
        }
        final String username = extractUsername(jwt);

        return JwtDto.builder()
                .accessToken(generateAccessToken(username))
                .refreshToken(generateRefreshToken(username))
                .build();
    }

    @Override
    public String extractJwt(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Builds a JSON Web Token (JWT) with the specified username and expiration time.
     *
     * @param username   The subject (username) to be included in the token.
     * @param expiration The duration in seconds for which the token will be valid.
     * @return The constructed JWT as a compact serialized string.
     */
    private String buildToken(final String username, final long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts a specific claim from a given JWT token using the provided claims resolver.
     *
     * @param token          The JWT token from which to extract the claim.
     * @param claimsResolver The function to resolve the desired claim from the JWT's body.
     * @param <T>            The type of the extracted claim.
     * @return The extracted claim.
     */
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
