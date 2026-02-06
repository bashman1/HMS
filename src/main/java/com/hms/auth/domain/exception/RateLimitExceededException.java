package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when rate limit is exceeded.
 */
public class RateLimitExceededException extends AuthException {

    public static final String ERROR_CODE = "RATE_LIMIT_EXCEEDED";

    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds) {
        super(String.format("Rate limit exceeded. Please try again after %d seconds.", retryAfterSeconds),
                HttpStatus.TOO_MANY_REQUESTS, ERROR_CODE);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, ERROR_CODE);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
