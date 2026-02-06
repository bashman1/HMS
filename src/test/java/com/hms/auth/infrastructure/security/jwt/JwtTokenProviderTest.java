package com.hms.auth.infrastructure.security.jwt;

import com.hms.auth.domain.model.entity.Permission;
import com.hms.auth.domain.model.entity.Role;
import com.hms.auth.domain.model.entity.User;
import com.hms.auth.infrastructure.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider.
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(
                "test-secret-key-for-testing-purposes-only-must-be-256-bits-long",
                900000L, // 15 minutes
                604800000L, // 7 days
                "hms-auth-service",
                "hms-services");
        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    private User createTestUser() {
        Permission readPermission = Permission.builder()
                .id(UUID.randomUUID())
                .name("users:read")
                .resource("users")
                .action("read")
                .build();

        Role userRole = Role.builder()
                .id(UUID.randomUUID())
                .name("USER")
                .build();
        userRole.addPermission(readPermission);

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("encoded-password")
                .firstName("Test")
                .lastName("User")
                .emailVerified(true)
                .enabled(true)
                .build();
        user.addRole(userRole);

        return user;
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid access token")
        void shouldGenerateValidAccessToken() {
            User user = createTestUser();
            UUID familyId = UUID.randomUUID();

            String accessToken = jwtTokenProvider.generateAccessToken(user);

            assertNotNull(accessToken);
            assertFalse(accessToken.isEmpty());
            assertTrue(jwtTokenProvider.validateToken(accessToken));
        }

        @Test
        @DisplayName("Should generate valid refresh token")
        void shouldGenerateValidRefreshToken() {
            User user = createTestUser();
            UUID familyId = UUID.randomUUID();

            String refreshToken = jwtTokenProvider.generateRefreshToken(user, familyId);

            assertNotNull(refreshToken);
            assertFalse(refreshToken.isEmpty());
            assertTrue(jwtTokenProvider.validateToken(refreshToken));
        }

        @Test
        @DisplayName("Access token should contain correct claims")
        void accessTokenShouldContainCorrectClaims() {
            User user = createTestUser();

            String accessToken = jwtTokenProvider.generateAccessToken(user);
            Claims claims = jwtTokenProvider.parseToken(accessToken);

            assertEquals(user.getId().toString(), claims.getSubject());
            assertEquals("hms-auth-service", claims.getIssuer());
            assertEquals("access", claims.get("type", String.class));
            assertEquals(user.getUsername(), claims.get("username", String.class));
            assertEquals(user.getEmail(), claims.get("email", String.class));
        }

        @Test
        @DisplayName("Refresh token should contain family ID")
        void refreshTokenShouldContainFamilyId() {
            User user = createTestUser();
            UUID familyId = UUID.randomUUID();

            String refreshToken = jwtTokenProvider.generateRefreshToken(user, familyId);

            Claims claims = jwtTokenProvider.parseToken(refreshToken);
            assertEquals("refresh", claims.get("type", String.class));
            assertEquals(familyId.toString(), claims.get("familyId", String.class));
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate valid token")
        void shouldValidateValidToken() {
            User user = createTestUser();
            String accessToken = jwtTokenProvider.generateAccessToken(user);

            assertTrue(jwtTokenProvider.validateToken(accessToken));
        }

        @Test
        @DisplayName("Should reject malformed token")
        void shouldRejectMalformedToken() {
            String malformedToken = "invalid.token.here";

            assertFalse(jwtTokenProvider.validateToken(malformedToken));
        }

        @Test
        @DisplayName("Should reject empty token")
        void shouldRejectEmptyToken() {
            assertFalse(jwtTokenProvider.validateToken(""));
            assertFalse(jwtTokenProvider.validateToken(null));
        }
    }

    @Nested
    @DisplayName("Token Parsing Tests")
    class TokenParsingTests {

        @Test
        @DisplayName("Should extract user ID from token")
        void shouldExtractUserIdFromToken() {
            User user = createTestUser();
            String accessToken = jwtTokenProvider.generateAccessToken(user);

            UUID extractedUserId = jwtTokenProvider.getUserIdFromToken(accessToken);

            assertEquals(user.getId(), extractedUserId);
        }

        @Test
        @DisplayName("Should extract token type from token")
        void shouldExtractTokenTypeFromToken() {
            User user = createTestUser();

            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user, UUID.randomUUID());

            assertEquals("access", jwtTokenProvider.getTokenType(accessToken));
            assertEquals("refresh", jwtTokenProvider.getTokenType(refreshToken));
        }

        @Test
        @DisplayName("Should extract token ID from token")
        void shouldExtractTokenIdFromToken() {
            User user = createTestUser();
            String accessToken = jwtTokenProvider.generateAccessToken(user);

            String tokenId = jwtTokenProvider.getTokenId(accessToken);

            assertNotNull(tokenId);
            assertFalse(tokenId.isEmpty());
        }

        @Test
        @DisplayName("Should extract expiration from token")
        void shouldExtractExpirationFromToken() {
            User user = createTestUser();
            String accessToken = jwtTokenProvider.generateAccessToken(user);

            var expiration = jwtTokenProvider.getExpirationFromToken(accessToken);

            assertNotNull(expiration);
            assertTrue(expiration.isAfter(java.time.Instant.now()));
        }
    }

    @Nested
    @DisplayName("Expiration Tests")
    class ExpirationTests {

        @Test
        @DisplayName("Should return correct access token expiration seconds")
        void shouldReturnCorrectAccessTokenExpirationSeconds() {
            long expectedExpiration = jwtProperties.getAccessTokenExpirationSeconds();
            long actualExpiration = jwtTokenProvider.getAccessTokenExpirationSeconds();

            assertEquals(expectedExpiration, actualExpiration);
        }

        @Test
        @DisplayName("Should return access token expiration instant in future")
        void shouldReturnAccessTokenExpirationInstantInFuture() {
            var beforeGeneration = java.time.Instant.now();
            var expirationInstant = jwtTokenProvider.getAccessTokenExpirationInstant();
            var afterGeneration = java.time.Instant.now();

            assertTrue(expirationInstant.isAfter(beforeGeneration));
            assertTrue(expirationInstant.isAfter(afterGeneration));
        }

        @Test
        @DisplayName("Should return refresh token expiration instant in future")
        void shouldReturnRefreshTokenExpirationInstantInFuture() {
            var beforeGeneration = java.time.Instant.now();
            var expirationInstant = jwtTokenProvider.getRefreshTokenExpirationInstant();
            var afterGeneration = java.time.Instant.now();

            assertTrue(expirationInstant.isAfter(beforeGeneration));
            assertTrue(expirationInstant.isAfter(afterGeneration));
        }
    }
}
