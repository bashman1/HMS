package com.hms.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user login.
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer with built-in validation.
 */
@Schema(description = "User login request")
public record LoginRequest(
        @Schema(description = "Username or email address", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Username or email is required") @Size(max = 255, message = "Username or email must not exceed 255 characters") String usernameOrEmail,

        @Schema(description = "Password", example = "SecureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Password is required") @Size(max = 100, message = "Password must not exceed 100 characters") String password,

        @Schema(description = "Remember me flag for extended session", example = "false") Boolean rememberMe) {
    /**
     * Returns whether remember me is enabled.
     *
     * @return true if remember me is enabled
     */
    public boolean isRememberMe() {
        return rememberMe != null && rememberMe;
    }
}
