package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Exception thrown when a user account is locked.
 */
public class AccountLockedException extends AuthException {

    public static final String ERROR_CODE = "ACCOUNT_LOCKED";

    private final Instant lockTime;
    private final long lockDurationSeconds;

    public AccountLockedException(Instant lockTime, long lockDurationSeconds) {
        super(String.format("Account is locked. Please try again after %d minutes.",
                lockDurationSeconds / 60), HttpStatus.FORBIDDEN, ERROR_CODE);
        this.lockTime = lockTime;
        this.lockDurationSeconds = lockDurationSeconds;
    }

    public AccountLockedException(String message) {
        super(message, HttpStatus.FORBIDDEN, ERROR_CODE);
        this.lockTime = null;
        this.lockDurationSeconds = 0;
    }

    public Instant getLockTime() {
        return lockTime;
    }

    public long getLockDurationSeconds() {
        return lockDurationSeconds;
    }

    public Instant getUnlockTime() {
        if (lockTime == null) {
            return null;
        }
        return lockTime.plusSeconds(lockDurationSeconds);
    }
}
