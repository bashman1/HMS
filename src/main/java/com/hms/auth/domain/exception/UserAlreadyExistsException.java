package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class UserAlreadyExistsException extends AuthException {

    public static final String ERROR_CODE = "USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, ERROR_CODE);
    }

    public UserAlreadyExistsException(String field, String value) {
        super(String.format("User already exists with %s: %s", field, value), HttpStatus.CONFLICT, ERROR_CODE);
    }
}
