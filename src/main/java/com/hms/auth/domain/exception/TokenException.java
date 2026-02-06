package com.hms.auth.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for token-related errors.
 */
public class TokenException extends AuthException {

    public static final String ERROR_CODE_INVALID = "INVALID_TOKEN";
    public static final String ERROR_CODE_EXPIRED = "TOKEN_EXPIRED";
    public static final String ERROR_CODE_REVOKED = "TOKEN_REVOKED";

    public TokenException(String message, String errorCode) {
        super(message, HttpStatus.UNAUTHORIZED, errorCode);
    }

    public static TokenException invalid(String message) {
        return new TokenException(message, ERROR_CODE_INVALID);
    }

    public static TokenException expired() {
        return new TokenException("Token has expired", ERROR_CODE_EXPIRED);
    }

    public static TokenException revoked() {
        return new TokenException("Token has been revoked", ERROR_CODE_REVOKED);
    }
}
