package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Exception thrown when password validation fails.
 */
public class PasswordValidationException extends AuthException {

    public static final String ERROR_CODE = "PASSWORD_VALIDATION_FAILED";

    private final List<String> violations;

    public PasswordValidationException(List<String> violations) {
        super("Password does not meet security requirements", HttpStatus.BAD_REQUEST, ERROR_CODE);
        this.violations = violations;
    }

    public PasswordValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ERROR_CODE);
        this.violations = List.of(message);
    }

    public List<String> getViolations() {
        return violations;
    }
}
