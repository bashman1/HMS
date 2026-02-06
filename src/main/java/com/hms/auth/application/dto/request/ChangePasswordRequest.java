package com.hms.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for changing password (authenticated user).
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer with built-in validation.
 */
@Schema(description = "Change password request")
public record ChangePasswordRequest(
        @Schema(description = "Current password", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Current password is required") String currentPassword,

        @Schema(description = "New password", example = "NewSecureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "New password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String newPassword,

        @Schema(description = "New password confirmation", example = "NewSecureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Password confirmation is required") String confirmPassword) {
    /**
     * Validates that newPassword and confirmPassword match.
     *
     * @return true if passwords match
     */
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

    /**
     * Validates that new password is different from current password.
     *
     * @return true if passwords are different
     */
    public boolean isNewPasswordDifferent() {
        return currentPassword != null && !currentPassword.equals(newPassword);
    }
}
