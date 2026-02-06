package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends AuthException {

    public static final String ERROR_CODE = "USER_NOT_FOUND";

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, ERROR_CODE);
    }

    public UserNotFoundException(String field, String value) {
        super(String.format("User not found with %s: %s", field, value), HttpStatus.NOT_FOUND, ERROR_CODE);
    }
}
