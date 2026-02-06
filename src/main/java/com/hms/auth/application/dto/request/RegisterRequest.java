package com.hms.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Request DTO for user registration.
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer with built-in validation.
 */
@Schema(description = "User registration request")
public record RegisterRequest(
        @Schema(description = "Username", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores") String username,

        @Schema(description = "Email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 255, message = "Email must not exceed 255 characters") String email,

        @Schema(description = "Password", example = "SecureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String password,

        @Schema(description = "Password confirmation", example = "SecureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Password confirmation is required") String confirmPassword,

        @Schema(description = "First name", example = "John") @Size(max = 100, message = "First name must not exceed 100 characters") String firstName,

        @Schema(description = "Last name", example = "Doe") @Size(max = 100, message = "Last name must not exceed 100 characters") String lastName,

        @Schema(description = "Phone number", example = "+1234567890") @Size(max = 20, message = "Phone number must not exceed 20 characters") @Pattern(regexp = "^\\+?[0-9\\s-]+$", message = "Invalid phone number format") String phoneNumber) {
    /**
     * Validates that password and confirmPassword match.
     *
     * @return true if passwords match
     */
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
