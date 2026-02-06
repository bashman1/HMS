package com.hms.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Generic message response DTO.
 * 
 * <p>
 * Uses Java 21 record for immutable data transfer.
 */
@Schema(description = "Generic message response")
public record MessageResponse(
        @Schema(description = "Response message") String message,

        @Schema(description = "Success status") boolean success,

        @Schema(description = "Response timestamp") Instant timestamp) {
    /**
     * Creates a success message response.
     *
     * @param message the message
     * @return the message response
     */
    public static MessageResponse success(String message) {
        return new MessageResponse(message, true, Instant.now());
    }

    /**
     * Creates a failure message response.
     *
     * @param message the message
     * @return the message response
     */
    public static MessageResponse failure(String message) {
        return new MessageResponse(message, false, Instant.now());
    }
}
