package com.hms.auth.infrastructure.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for JWT settings.
 */
@ConfigurationProperties(prefix = "app.jwt")
@Validated
public record JwtProperties(
        @NotBlank(message = "JWT secret is required") String secret,

        @Positive(message = "Access token expiration must be positive") long accessTokenExpiration,

        @Positive(message = "Refresh token expiration must be positive") long refreshTokenExpiration,

        String issuer,

        String audience) {
    /**
     * Gets access token expiration in seconds.
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }

    /**
     * Gets refresh token expiration in seconds.
     */
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration / 1000;
    }
}
