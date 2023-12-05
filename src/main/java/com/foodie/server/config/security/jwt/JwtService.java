package com.foodie.server.config.security.jwt;

import com.foodie.server.exception.custom.JwtNotFoundException;
import com.foodie.server.model.dto.JwtDto;

import java.util.Date;

public interface JwtService {
    /**
     * Generates an access token for the given username using JSON Web Tokens (JWT).
     *
     * @param username The username for which the access token is generated.
     * @return The generated access token.
     */
    String generateAccessToken(String username);

    /**
     * Generates a refresh token for the given username using JSON Web Tokens (JWT).
     *
     * @param username The username for which the refresh token is generated.
     * @return The generated refresh token.
     */
    String generateRefreshToken(String username);

    /**
     * Generates an Authorization header value for a given JWT token in the "Bearer" token format.
     *
     * @param token The JWT token for which to generate the Authorization header.
     * @return The Authorization header value in the "Bearer" token format.
     */
    String generateHeader(String token);

    /**
     * Refreshes "access" and "refresh" tokens based on the provided JWT in the Authorization header.
     *
     * @param authorizationHeader The authorization header containing the JWT.
     * @return A JwtDto containing the newly generated access and refresh tokens.
     * @throws JwtNotFoundException If the JWT is not found in the authorization header.
     */
    JwtDto refreshTokens(String authorizationHeader);

    /**
     * Extracts the JWT from the Authorization header.
     *
     * @param authorizationHeader The Authorization header containing the JWT.
     * @return The extracted JWT or null if the header is missing or does not start with "Bearer ".
     */
    String extractJwt(String authorizationHeader);

    /**
     * Extracts the username from a given JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The extracted username.
     */
    String extractUsername(String token);

    /**
     * Extracts the expiration date from a given JWT token.
     *
     * @param token The JWT token from which to extract the expiration date.
     * @return The extracted expiration date.
     */
    Date extractExpiration(String token);

    /**
     * Checks if a JWT token is valid by comparing the extracted username with the expected username
     * and verifying that the token has not expired.
     *
     * @param token            The JWT token to be validated.
     * @param expectedUsername The username expected to be in the token.
     * @return True if the token is valid, false otherwise.
     */
    boolean isTokenValid(String token, String expectedUsername);
}
