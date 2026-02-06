package com.hms.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for initiating password reset.
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer with built-in validation.
 */
@Schema(description = "Password reset initiation request")
public record PasswordResetRequest(
        @Schema(description = "Email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 255, message = "Email must not exceed 255 characters") String email) {
}
