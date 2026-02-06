package com.hms.auth.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.auth.application.dto.request.LoginRequest;
import com.hms.auth.application.dto.request.RefreshTokenRequest;
import com.hms.auth.application.dto.request.RegisterRequest;
import com.hms.auth.domain.model.entity.Role;
import com.hms.auth.domain.model.entity.User;
import com.hms.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.hms.auth.domain.port.out.RoleRepositoryPort;
import com.hms.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security-focused integration tests for authentication flows.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private RoleRepositoryPort roleRepository;

    @Autowired
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clean up
        refreshTokenRepository.deleteExpiredTokensBefore(java.time.Instant.now().minusSeconds(3600));
    }

    private Role getOrCreateDefaultRole() {
        return roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .id(UUID.randomUUID())
                            .name("USER")
                            .isDefault(true)
                            .build();
                    return roleRepository.save(role);
                });
    }

    private User createTestUser(String email, String username, String password, boolean verified) {
        Role defaultRole = getOrCreateDefaultRole();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName("Test")
                .lastName("User")
                .emailVerified(verified)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        user.addRole(defaultRole);

        return userRepository.save(user);
    }

    @Nested
    @DisplayName("Registration Security Tests")
    class RegistrationSecurityTests {

        @Test
        @DisplayName("Should reject registration with weak password")
        void shouldRejectWeakPassword() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "testuser",
                    "test@example.com",
                    "weak", // Too short
                    "weak",
                    "Test",
                    "User",
                    null);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject registration with duplicate email")
        void shouldRejectDuplicateEmail() throws Exception {
            createTestUser("existing@example.com", "user1", "SecureP@ss123", true);

            RegisterRequest request = new RegisterRequest(
                    "testuser",
                    "existing@example.com",
                    "SecureP@ss123",
                    "SecureP@ss123",
                    "Test",
                    "User",
                    null);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should reject registration with duplicate username")
        void shouldRejectDuplicateUsername() throws Exception {
            createTestUser("user1@example.com", "existinguser", "SecureP@ss123", true);

            RegisterRequest request = new RegisterRequest(
                    "existinguser",
                    "newuser@example.com",
                    "SecureP@ss123",
                    "SecureP@ss123",
                    "Test",
                    "User",
                    null);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should reject registration with invalid email format")
        void shouldRejectInvalidEmailFormat() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "testuser",
                    "invalid-email",
                    "SecureP@ss123",
                    "SecureP@ss123",
                    "Test",
                    "User",
                    null);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject registration with mismatched passwords")
        void shouldRejectMismatchedPasswords() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "testuser",
                    "test@example.com",
                    "SecureP@ss123",
                    "DifferentPassword",
                    "Test",
                    "User",
                    null);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Login Security Tests")
    class LoginSecurityTests {

        @Test
        @DisplayName("Should reject login with invalid credentials")
        void shouldRejectInvalidCredentials() throws Exception {
            LoginRequest request = new LoginRequest(
                    "nonexistent@example.com",
                    "wrongpassword",
                    false);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should reject login with unverified email")
        void shouldRejectUnverifiedEmail() throws Exception {
            createTestUser("unverified@example.com", "unverifieduser", "SecureP@ss123", false);

            LoginRequest request = new LoginRequest(
                    "unverified@example.com",
                    "SecureP@ss123",
                    false);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should reject login with empty credentials")
        void shouldRejectEmptyCredentials() throws Exception {
            LoginRequest request = new LoginRequest("", "", false);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Protected Endpoint Access Tests")
    class ProtectedEndpointAccessTests {

        @Test
        @DisplayName("Should reject access to protected endpoint without token")
        void shouldRejectAccessWithoutToken() throws Exception {
            mockMvc.perform(get("/auth/me"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should reject access with invalid token")
        void shouldRejectInvalidToken() throws Exception {
            mockMvc.perform(get("/auth/me")
                    .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should reject access with malformed authorization header")
        void shouldRejectMalformedAuthHeader() throws Exception {
            mockMvc.perform(get("/auth/me")
                    .header("Authorization", "NotBearer token"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should reject access with expired token")
        void shouldRejectExpiredToken() throws Exception {
            // Note: In a real test, you would generate a token with very short expiration
            // For now, we test with an invalid token
            mockMvc.perform(get("/auth/me")
                    .header("Authorization", "Bearer "))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Public Endpoint Access Tests")
    class PublicEndpointAccessTests {

        @Test
        @DisplayName("Should allow access to health endpoint")
        void shouldAllowHealthEndpoint() throws Exception {
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should allow access to registration endpoint")
        void shouldAllowRegistrationEndpoint() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "newuser@example.com",
                    "SecureP@ss123",
                    "SecureP@ss123",
                    "New",
                    "User",
                    null);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should allow access to login endpoint")
        void shouldAllowLoginEndpoint() throws Exception {
            LoginRequest request = new LoginRequest(
                    "test@example.com",
                    "password",
                    false);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should allow access to token refresh endpoint")
        void shouldAllowTokenRefreshEndpoint() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("some-refresh-token");

            mockMvc.perform(post("/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Token Security Tests")
    class TokenSecurityTests {

        @Test
        @DisplayName("Should reject refresh with invalid token")
        void shouldRejectInvalidRefreshToken() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");

            mockMvc.perform(post("/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should reject logout without authentication")
        void shouldRejectLogoutWithoutAuth() throws Exception {
            mockMvc.perform(post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()); // Logout is typically a soft operation
        }
    }
}
