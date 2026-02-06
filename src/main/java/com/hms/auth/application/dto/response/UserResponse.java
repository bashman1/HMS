package com.hms.auth.application.dto.response;

import com.hms.auth.domain.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Response DTO for user information.
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer.
 */
@Schema(description = "User information response")
public record UserResponse(
        @Schema(description = "User ID") UUID id,

        @Schema(description = "Username") String username,

        @Schema(description = "Email address") String email,

        @Schema(description = "First name") String firstName,

        @Schema(description = "Last name") String lastName,

        @Schema(description = "Full name") String fullName,

        @Schema(description = "Phone number") String phoneNumber,

        @Schema(description = "Email verification status") boolean emailVerified,

        @Schema(description = "Account enabled status") boolean enabled,

        @Schema(description = "User roles") Set<String> roles,

        @Schema(description = "User permissions") Set<String> permissions,

        @Schema(description = "Last login timestamp") Instant lastLoginAt,

        @Schema(description = "Account creation timestamp") Instant createdAt) {
    /**
     * Creates a UserResponse from a User entity.
     *
     * @param user the user entity
     * @return the user response
     */
    public static UserResponse fromEntity(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.isEmailVerified(),
                user.isEnabled(),
                roles,
                permissions,
                user.getLastLoginAt(),
                user.getCreatedAt());
    }
}
