package com.hms.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response DTO for authentication operations.
 * 
 * <p>
 * Contains access token, refresh token, and user information.
 * Uses Java 21 record for immutable data transfer.
 */
@Schema(description = "Authentication response with tokens and user info")
public record AuthResponse(
        @Schema(description = "JWT access token") String accessToken,

        @Schema(description = "Refresh token for obtaining new access tokens") String refreshToken,

        @Schema(description = "Token type", example = "Bearer") String tokenType,

        @Schema(description = "Access token expiration time in seconds") long expiresIn,

        @Schema(description = "Access token expiration timestamp") Instant expiresAt,

        @Schema(description = "User information") UserResponse user) {
    /**
     * Creates an AuthResponse with Bearer token type.
     *
     * @param accessToken  the access token
     * @param refreshToken the refresh token
     * @param expiresIn    expiration time in seconds
     * @param expiresAt    expiration timestamp
     * @param user         the user response
     * @return the auth response
     */
    public static AuthResponse of(String accessToken, String refreshToken,
            long expiresIn, Instant expiresAt, UserResponse user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, expiresAt, user);
    }
}
