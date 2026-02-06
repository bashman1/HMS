package com.hms.auth.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for security settings.
 */
@ConfigurationProperties(prefix = "app.security")
@Validated
public record SecurityProperties(
        CorsProperties cors,
        RateLimitingProperties rateLimiting,
        BruteForceProperties bruteForce,
        PasswordProperties password) {
    /**
     * CORS configuration properties.
     */
    public record CorsProperties(
            List<String> allowedOrigins,
            String allowedMethods,
            String allowedHeaders,
            String exposedHeaders,
            boolean allowCredentials,
            long maxAge) {
        public List<String> getAllowedMethodsList() {
            return List.of(allowedMethods.split(","));
        }

        public List<String> getAllowedHeadersList() {
            return "*".equals(allowedHeaders) ? List.of("*") : List.of(allowedHeaders.split(","));
        }

        public List<String> getExposedHeadersList() {
            return List.of(exposedHeaders.split(","));
        }
    }

    /**
     * Rate limiting configuration properties.
     */
    public record RateLimitingProperties(
            boolean enabled,
            RateLimitConfig login,
            RateLimitConfig api) {
        public record RateLimitConfig(
                int capacity,
                int refillTokens,
                long refillDuration) {
        }
    }

    /**
     * Brute force protection configuration properties.
     */
    public record BruteForceProperties(
            int maxAttempts,
            long lockDuration) {
    }

    /**
     * Password policy configuration properties.
     */
    public record PasswordProperties(
            int minLength,
            boolean requireUppercase,
            boolean requireLowercase,
            boolean requireDigit,
            boolean requireSpecial) {
    }
}
