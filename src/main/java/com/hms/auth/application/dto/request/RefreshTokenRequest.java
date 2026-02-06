package com.hms.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for refreshing access tokens.
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer with built-in validation.
 */
@Schema(description = "Token refresh request")
public record RefreshTokenRequest(
        @Schema(description = "Refresh token", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Refresh token is required") String refreshToken) {
}
