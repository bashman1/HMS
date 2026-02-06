package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication credentials are invalid.
 */
public class InvalidCredentialsException extends AuthException {

    public static final String ERROR_CODE = "INVALID_CREDENTIALS";

    public InvalidCredentialsException() {
        super("Invalid username or password", HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }
}
